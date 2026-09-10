// Character-level Java scanner.
//
// One pass over the source, tracking a mode and brace depth, emitting ordered
// segments. Downstream phases only ever look at segments, never at raw text.
//
// The reason this is a real lexer and not a line scanner: brace depth is what
// separates the comment regions (see structure.mjs), and a single `{` or `}`
// inside a string literal would silently corrupt it for the whole file.

const MODE = {
  CODE: 'CODE',
  LINE: 'LINE',
  BLOCK: 'BLOCK',
  JAVADOC: 'JAVADOC',
  STRING: 'STRING',
  CHAR: 'CHAR',
  TEXT_BLOCK: 'TEXT_BLOCK',
};

/**
 * @param {string} source  file text, already newline-normalized
 * @returns {{segments: Array, braces: Array, endDepth: number, maxDepth: number}}
 *   segments: [{ type: 'code'|'line'|'block'|'javadoc', startLine, endLine, depth, text }]
 *   braces:   [{ line, depth }] one entry per real `{`/`}`, depth AFTER the token.
 *             This is the single authoritative source of brace structure —
 *             callers must never re-count braces from text.
 *   endDepth: brace depth at EOF — must be 0 for well-formed Java
 */
export function lex(source) {
  const segments = [];
  const braces = [];
  let mode = MODE.CODE;
  let depth = 0;
  let maxDepth = 0;
  let line = 1;

  // start of the segment currently being accumulated
  let segStart = 0;
  let segStartLine = 1;
  let segDepth = 0;

  const at = (i) => source[i];
  const isAt = (i, s) => source.startsWith(s, i);

  function close(type, endIndex, endLine) {
    if (endIndex <= segStart) return;
    const text = source.slice(segStart, endIndex);
    // A code segment that is nothing but whitespace carries no information.
    if (type === 'code' && text.trim() === '') return;
    segments.push({ type, startLine: segStartLine, endLine, depth: segDepth, text });
  }

  function open(index, atLine) {
    segStart = index;
    segStartLine = atLine;
    segDepth = depth;
  }

  let i = 0;
  const n = source.length;

  while (i < n) {
    const c = at(i);

    if (c === '\n') {
      if (mode === MODE.LINE) {
        close('line', i, line);
        mode = MODE.CODE;
        line++;
        i++;
        open(i, line);
        continue;
      }
      if (mode === MODE.STRING || mode === MODE.CHAR) {
        // Unterminated literal. Malformed Java; recover rather than run away.
        mode = MODE.CODE;
      }
      line++;
      i++;
      continue;
    }

    switch (mode) {
      case MODE.CODE: {
        if (isAt(i, '"""')) {
          mode = MODE.TEXT_BLOCK;
          i += 3;
          continue;
        }
        if (c === '"') {
          mode = MODE.STRING;
          i++;
          continue;
        }
        if (c === "'") {
          mode = MODE.CHAR;
          i++;
          continue;
        }
        if (isAt(i, '//')) {
          close('code', i, line);
          mode = MODE.LINE;
          open(i, line);
          i += 2;
          continue;
        }
        // `/**/` is an empty block comment, not a javadoc opener.
        if (isAt(i, '/**') && at(i + 3) !== '/') {
          close('code', i, line);
          mode = MODE.JAVADOC;
          open(i, line);
          i += 3;
          continue;
        }
        if (isAt(i, '/*')) {
          close('code', i, line);
          mode = MODE.BLOCK;
          open(i, line);
          i += 2;
          continue;
        }
        if (c === '{') {
          depth++;
          if (depth > maxDepth) maxDepth = depth;
          braces.push({ line, depth });
          i++;
          continue;
        }
        if (c === '}') {
          depth--;
          braces.push({ line, depth });
          i++;
          continue;
        }
        i++;
        continue;
      }

      case MODE.STRING: {
        if (c === '\\') { i += 2; continue; }
        if (c === '"') { mode = MODE.CODE; i++; continue; }
        i++;
        continue;
      }

      case MODE.CHAR: {
        if (c === '\\') { i += 2; continue; }
        if (c === "'") { mode = MODE.CODE; i++; continue; }
        i++;
        continue;
      }

      case MODE.TEXT_BLOCK: {
        if (c === '\\') { i += 2; continue; }
        if (isAt(i, '"""')) { mode = MODE.CODE; i += 3; continue; }
        i++;
        continue;
      }

      case MODE.BLOCK:
      case MODE.JAVADOC: {
        if (isAt(i, '*/')) {
          i += 2;
          close(mode === MODE.JAVADOC ? 'javadoc' : 'block', i, line);
          mode = MODE.CODE;
          open(i, line);
          continue;
        }
        i++;
        continue;
      }

      default:
        i++;
    }
  }

  // Flush whatever was open at EOF.
  if (mode === MODE.LINE) close('line', n, line);
  else if (mode === MODE.JAVADOC) close('javadoc', n, line);
  else if (mode === MODE.BLOCK) close('block', n, line);
  else close('code', n, line);

  return { segments: mergeCommentRuns(segments), braces, endDepth: depth, maxDepth };
}

/**
 * Merge runs of adjacent `line` segments that sit on consecutive lines at the
 * same brace depth into a single segment. This is what turns the four stacked
 * `//` lines at the top of a class into one header unit, while leaving isolated
 * `//case 1` inline comments as their own segments.
 *
 * The merged segment keeps type 'line' and gains `lines: string[]` holding each
 * original comment's text.
 */
function mergeCommentRuns(segments) {
  const out = [];
  for (const seg of segments) {
    const prev = out[out.length - 1];
    if (
      seg.type === 'line' &&
      prev &&
      prev.type === 'line' &&
      prev.depth === seg.depth &&
      seg.startLine === prev.endLine + 1
    ) {
      prev.endLine = seg.endLine;
      prev.text += '\n' + seg.text;
      prev.lines.push(seg.text);
      continue;
    }
    if (seg.type === 'line') out.push({ ...seg, lines: [seg.text] });
    else out.push(seg);
  }
  return out;
}

/** Strip `//` from one raw line-comment line. */
export function stripLineComment(text) {
  return text.replace(/^\s*\/\/+/, '').trim();
}
