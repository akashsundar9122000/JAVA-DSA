// Structural anchors + comment-region partitioning.
//
// Anchors come from `code` segments and the lexer's brace events only. That is
// the whole point of lexing first:
//   - a commented-out `//class AuctionSystem {` can never be read as a decl
//   - a javadoc used to comment out code (`/**for(...){` ... `}**/`, as in
//     Graph/FindEventualState.java) cannot corrupt brace depth
// Nothing in this file may re-count braces from raw text.

const TYPE_DECL = /\b(class|interface|enum|record)\s+([A-Za-z_$][\w$]*)/;
const NESTED_DECL = /^\s*(?:(?:public|private|protected|static|final|abstract|sealed|non-sealed)\s+)*(?:class|interface|enum|record)\s+([A-Za-z_$][\w$]*)/;
const PACKAGE = /^\s*package\s+([\w.]+)\s*;/m;
const IMPORT = /^[ \t]*import\s+(?:static\s+)?([\w.*]+)\s*;/gm;

// A method/constructor signature: ends in `{` and carries a parameter list.
const SIGNATURE = /^\s*(?:(?:public|private|protected|static|final|abstract|synchronized|native|default|strictfp)\s+)*(?:<[^>]*>\s*)?(?:[\w$.<>[\],?\s]+\s+)?([A-Za-z_$][\w$]*)\s*\(([^)]*)\)\s*(?:throws\s+[\w$.,\s]+)?\s*\{/;

const NOT_A_METHOD = new Set([
  'if', 'for', 'while', 'switch', 'catch', 'try', 'else', 'do',
  'synchronized', 'return', 'new',
]);

/**
 * @param {string} source
 * @param {{segments: Array, braces: Array}} lexed
 * @param {string} fileBase basename without .java
 */
export function analyze(source, lexed, fileBase) {
  const { segments, braces } = lexed;
  const lines = source.split('\n');
  const code = segments.filter((s) => s.type === 'code');

  // --- package -------------------------------------------------------------
  let packageName = null;
  for (const s of code) {
    const m = s.text.match(PACKAGE);
    if (m) { packageName = m[1]; break; }
  }

  // --- imports -------------------------------------------------------------
  const imports = [];
  for (const s of code) {
    for (const m of s.text.matchAll(IMPORT)) imports.push(m[1]);
  }

  // --- primary type --------------------------------------------------------
  // First depth-0 code segment declaring a type. There are no second top-level
  // types in this corpus, so "first at depth 0" is unconditionally correct.
  let className = null;
  let typeKind = null;
  let typeLine = null;
  let implicitClass = false;

  for (const s of code) {
    if (s.depth !== 0) continue;
    const m = s.text.match(TYPE_DECL);
    if (!m) continue;
    typeLine = s.startLine + countNewlines(s.text.slice(0, m.index));
    typeKind = m[1];
    className = m[2];
    break;
  }

  if (!className) {
    // JDK 25 implicitly declared class (src/Main.java): no `class` keyword.
    implicitClass = true;
    className = fileBase;
    typeKind = 'class';
    typeLine = 1;
  }

  // --- class body braces, straight from the lexer --------------------------
  let classBodyOpenLine;
  let classBodyCloseLine;

  if (implicitClass) {
    classBodyOpenLine = 0;
    classBodyCloseLine = lines.length;
  } else {
    const openIdx = braces.findIndex((b) => b.line >= typeLine && b.depth === 1);
    if (openIdx === -1) {
      classBodyOpenLine = typeLine;
      classBodyCloseLine = lines.length;
    } else {
      classBodyOpenLine = braces[openIdx].line;
      const close = braces.slice(openIdx + 1).find((b) => b.depth === 0);
      classBodyCloseLine = close ? close.line : lines.length;
    }
  }

  // --- firstMemberLine -----------------------------------------------------
  // The load-bearing boundary between class-level ("header") comments and
  // everything else. It counts fields and nested classes too, not just
  // methods: Heap/HeapLecture.java declares a field first and its class doc
  // must still land in HEADER.
  const commentOnly = buildCommentOnlyMask(segments, lines.length);
  const memberDepth = implicitClass ? 0 : 1;
  let firstMemberLine = classBodyCloseLine;

  for (const s of code) {
    if (s.depth !== memberDepth) continue;
    if (s.startLine <= classBodyOpenLine || s.startLine >= classBodyCloseLine) continue;
    const ln = firstMeaningfulLine(s, lines, commentOnly, classBodyOpenLine);
    if (ln !== null && ln < firstMemberLine) firstMemberLine = ln;
  }

  if (implicitClass) {
    let lastImport = 0;
    lines.forEach((l, idx) => { if (/^\s*import\s/.test(l)) lastImport = idx + 1; });
    if (firstMemberLine === classBodyCloseLine) firstMemberLine = lastImport + 1;
    firstMemberLine = Math.max(firstMemberLine, lastImport + 1);
  }

  // --- signatures & nested classes ----------------------------------------
  const signatureLines = [];
  const nestedClasses = [];
  let mainKind = 'none';

  // Start strictly after the class's opening brace, otherwise the primary
  // declaration itself matches NESTED_DECL and every file reports one.
  const bodyLo = implicitClass ? 1 : classBodyOpenLine + 1;
  const bodyHi = implicitClass ? lines.length : classBodyCloseLine;

  for (let ln = bodyLo; ln <= bodyHi; ln++) {
    if (commentOnly[ln]) continue;
    const raw = lines[ln - 1] ?? '';

    const nested = raw.match(NESTED_DECL);
    if (nested) {
      nestedClasses.push({ name: nested[1], line: ln, text: raw.trim() });
      continue;
    }

    const sig = raw.match(SIGNATURE);
    if (!sig) continue;
    const name = sig[1];
    if (NOT_A_METHOD.has(name)) continue;
    const isMain = name === 'main';
    if (isMain) mainKind = sig[2].trim() === '' ? 'compact' : 'args';
    signatureLines.push({
      line: ln,
      text: raw.trim(),
      name,
      kind: isMain ? 'main' : name === className ? 'constructor' : 'method',
    });
  }

  if (implicitClass && /^\s*(?:static\s+)?void\s+main\s*\(\s*\)/m.test(source)) mainKind = 'compact';

  return {
    lines,
    packageName,
    imports: [...new Set(imports)],
    className,
    typeKind,
    typeLine,
    implicitClass,
    classBodyOpenLine,
    classBodyCloseLine,
    firstMemberLine,
    signatureLines,
    nestedClasses,
    mainKind,
  };
}

/**
 * Partition every comment segment into the four regions.
 *
 * PREAMBLE : ends before the type declaration
 * HEADER   : between the class's `{` and its first member  <- all metadata
 * MEMBER   : inside the class body, at or after the first member
 * TRAILING : after the class's closing brace
 */
export function partitionComments(segments, a) {
  const regions = { preamble: [], header: [], member: [], trailing: [] };
  for (const s of segments) {
    if (s.type === 'code') continue;
    if (a.implicitClass) {
      (s.startLine < a.firstMemberLine ? regions.header : regions.member).push(s);
      continue;
    }
    if (s.endLine < a.typeLine) regions.preamble.push(s);
    else if (s.startLine > a.classBodyCloseLine) regions.trailing.push(s);
    else if (s.startLine > a.classBodyOpenLine && s.startLine < a.firstMemberLine) regions.header.push(s);
    else regions.member.push(s);
  }
  return regions;
}

// --- helpers ---------------------------------------------------------------

function countNewlines(s) {
  let n = 0;
  for (let i = 0; i < s.length; i++) if (s[i] === '\n') n++;
  return n;
}

/** line number -> true when the line holds no code at all. */
function buildCommentOnlyMask(segments, lineCount) {
  const mask = new Array(lineCount + 2).fill(false);
  const codeLines = new Set();
  for (const s of segments) {
    if (s.type !== 'code') continue;
    // A code segment's first/last line can be shared with a comment; only
    // count a line as code-bearing if the segment contributes non-space there.
    for (let ln = s.startLine; ln <= s.endLine; ln++) codeLines.add(ln);
  }
  for (const s of segments) {
    if (s.type === 'code') continue;
    for (let ln = s.startLine; ln <= s.endLine; ln++) {
      if (!codeLines.has(ln)) mask[ln] = true;
    }
  }
  return mask;
}

/**
 * First line of a code segment that carries real member text. Skips blank
 * lines, lone closing punctuation, and the comment-closer remnant a code
 * segment inherits when it begins mid-line, right after a doc comment ends.
 */
function firstMeaningfulLine(seg, lines, commentOnly, afterLine) {
  for (let ln = Math.max(seg.startLine, afterLine + 1); ln <= seg.endLine; ln++) {
    if (commentOnly[ln]) continue;
    let text = lines[ln - 1] ?? '';
    text = text
      .replace(/"(?:\\.|[^"\\])*"/g, '""')
      .replace(/'(?:\\.|[^'\\])*'/g, "''")
      .replace(/\/\*.*?\*\//g, '')
      .replace(/\/\/.*$/, '');
    // Remnant of a comment closer that ended on this line.
    text = text.replace(/^[\s}\])]*\*+\//, '');
    if (text.trim() === '') continue;
    if (/^[\s})\];]*$/.test(text)) continue;
    return ln;
  }
  return null;
}
