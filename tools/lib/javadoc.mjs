// Javadoc normalization and sectioning.
//
// Two stages, deliberately separate:
//   normalizeDoc() turns a raw /** ... */ segment into clean text
//   sectionDoc()   turns clean text into { statement, examples, constraints }
//
// The corpus is mostly verbatim LeetCode pastes, which arrive with blank-line
// padding, mangled superscripts, zero-width spaces and a watermark.

const ZERO_WIDTH = /[​‌‍﻿]/g;

// Paste artifacts that eat the first character of a statement. Applied at
// position 0 only, so they cannot corrupt mid-text prose.
const FIRST_CHAR_REPAIRS = [
  [/^ou (are|have|may|can|will|need|must) /, 'You $1 '],
  [/^ou're /, "You're "],
  [/^here (is|are) /, 'There $1 '],
  [/^he ([a-z])/, 'The $1'],
  [/^iven /, 'Given '],
  [/^eturn /, 'Return '],
  [/^mplement /, 'Implement '],
  [/^esign /, 'Design '],
  [/^onsider /, 'Consider '],
  [/^uppose /, 'Suppose '],
  [/^rite /, 'Write '],
  [/^ind /, 'Find '],
];

/**
 * Raw javadoc segment text -> normalized plain text (or null when empty).
 */
export function normalizeDoc(raw) {
  let text = raw;

  // Strip the delimiters. Content may begin immediately after `/**` with no
  // space (Graph/FindEventualState.java) and may close with `**/`.
  text = text.replace(/^\s*\/\*\*+/, '').replace(/\*+\/\s*$/, '');

  const repairs = [];

  let lines = text.split('\n').map((line) => {
    let out = line.replace(/\t/g, '    ');
    // Drop the javadoc framing: the indentation up to and including the `*`
    // is not content. The `*` is OPTIONAL — the javadoc at
    // Graph/CourseScheduleOrCycleInDirectedGraph.java:47 has body lines with
    // no asterisk at all, only indentation.
    const framed = out.match(/^[ ]*\*(?!\/)/);
    if (framed) {
      out = out.slice(framed[0].length);
      // Exactly one space after the `*` is framing too; anything beyond it is
      // meaningful hanging indentation.
      out = out.replace(/^ /, '');
    }
    return out.replace(/\s+$/, '');
  });

  // Lines that carried no `*` still hold the block's base indentation. Remove
  // the common indent so relative structure survives but framing does not.
  const indents = lines
    .filter((l) => l.trim() !== '')
    .map((l) => l.match(/^ */)[0].length);
  const base = indents.length ? Math.min(...indents) : 0;
  if (base > 0) lines = lines.map((l) => (l.trim() === '' ? l : l.slice(base)));

  // Unicode cleanup.
  lines = lines.map((l) =>
    l
      .replace(ZERO_WIDTH, '')
      .replace(/ /g, ' ')
      .replace(/≤/g, '<=')
      .replace(/≥/g, '>=')
      .replace(/≠/g, '!=')
      .replace(/→/g, '->')
      .replace(/‘|’/g, "'")
      .replace(/“|”/g, '"')
      .replace(/–|—/g, '-')
      .replace(/©\s*leetcode/gi, '')
      .replace(/\s+$/, ''),
  );

  // Collapse LeetCode's `*`-padding: runs of >=2 blank lines become one.
  const collapsed = [];
  let blanks = 0;
  for (const l of lines) {
    if (l.trim() === '') { blanks++; continue; }
    if (blanks > 0 && collapsed.length > 0) collapsed.push('');
    blanks = 0;
    collapsed.push(l);
  }

  if (collapsed.length === 0) return null;

  let body = collapsed.join('\n').trim();
  if (body === '') return null;

  // Repair a truncated first character.
  for (const [re, sub] of FIRST_CHAR_REPAIRS) {
    if (re.test(body)) {
      body = body.replace(re, sub);
      repairs.push('leading-char');
      break;
    }
  }

  return { text: body, repairs };
}

/**
 * Restore superscripts LeetCode's HTML-to-text mangling flattened.
 *
 * Only unambiguous shapes, never a guess:
 *   10^1..10^9 and 10^10..10^18  (so `104` -> `10^4`, but `100`/`1000` never)
 *   2^31 2^32 2^63 2^64
 * A token preceded by a digit or a decimal point is never touched.
 *
 * Returns { text, substitutions[] } so every change stays auditable.
 */
export function restoreSuperscripts(line) {
  const substitutions = [];
  let out = line;

  const apply = (re, fn) => {
    out = out.replace(re, (...args) => {
      const replaced = fn(...args);
      substitutions.push({ from: args[0], to: replaced });
      return replaced;
    });
  };

  apply(/(?<![\d.^])10(1[0-8])(?!\d)/g, (_m, d) => `10^${d}`);
  apply(/(?<![\d.^])10([1-9])(?!\d)/g, (_m, d) => `10^${d}`);
  apply(/(?<![\d.^])2(31|32|63|64)(?!\d)/g, (_m, d) => `2^${d}`);

  return { text: out, substitutions };
}

const RE = {
  contestQ: /^Q(\d+)\.\s+(.+)$/,
  contestStatus: /^(Solved|Attempted|Unsolved)$/i,
  contestPoints: /^(\d+)\s*pt\.?$/i,
  difficulty: /^(Easy|Medium|Hard)$/i,
  exampleN: /^Example\s*(\d+)\s*:?\s*$/i,
  exampleAny: /^Examples?\s*:?\s*$/i,
  constraints: /^Constraints?\s*:?\s*$/i,
  followUp: /^Follow[-\s]?up\s*:?\s*(.*)$/i,
  note: /^Note\s*:\s*(.*)$/i,
  input: /^Input\s*[:=]\s*(.*)$/i,
  output: /^Output\s*[:=]\s*(.*)$/i,
  explanation: /^Explanation\s*[:=]\s*(.*)$/i,
  imagePlaceholder: /^(!\[|Image\b|\[image\])/i,
};

/**
 * Normalized text -> structured sections.
 *
 * Blank lines BUFFER rather than close the currently open field. That single
 * decision is what lets both corpus layouts run through one code path:
 *   blank-separated  (SlidingWindow/PowerOfKsubArrays.java)
 *   adjacent         (BinarySearch/SingleElementInSortedarrya.java)
 */
export function sectionDoc(text) {
  const lines = text.split('\n');

  const res = {
    statement: '',
    examples: [],
    constraints: [],
    constraintsRaw: [],
    followUp: null,
    difficulty: null,
    pasteTitle: null,
    contestQuestionNo: null,
    contestPoints: null,
    superscriptSubs: [],
  };

  let state = 'PROLOGUE';
  const prologue = [];
  let example = null;
  let field = null; // 'input' | 'output' | 'explanation' | 'extra'
  const followUpLines = [];

  const newExample = (index) => {
    flushExample();
    example = { index, input: '', output: '', explanation: '', extra: '' };
    field = null;
  };

  function flushExample() {
    if (!example) return;
    const e = {
      index: example.index,
      input: tidy(example.input),
      output: tidy(example.output),
      explanation: tidy(example.explanation) || null,
      extra: tidy(example.extra) || null,
    };
    if (e.input || e.output || e.explanation || e.extra) res.examples.push(e);
    example = null;
    field = null;
  }

  for (let i = 0; i < lines.length; i++) {
    const raw = lines[i];
    const t = raw.trim();

    // --- contest chrome, first few lines only ------------------------------
    if (i < 6) {
      const q = t.match(RE.contestQ);
      if (q && i === 0) {
        res.contestQuestionNo = Number(q[1]);
        res.pasteTitle = q[2].trim();
        continue;
      }
      if (RE.contestStatus.test(t)) continue;
      const pts = t.match(RE.contestPoints);
      if (pts) { res.contestPoints = Number(pts[1]); continue; }
      if (RE.difficulty.test(t) && state === 'PROLOGUE' && prologue.length === 0) {
        res.difficulty = t[0].toUpperCase() + t.slice(1).toLowerCase();
        continue;
      }
    }

    // --- section headers ---------------------------------------------------
    const exN = t.match(RE.exampleN);
    if (exN) { state = 'EXAMPLE'; newExample(Number(exN[1])); continue; }
    if (RE.exampleAny.test(t)) { state = 'EXAMPLE'; newExample(null); continue; }
    if (RE.constraints.test(t)) { flushExample(); state = 'CONSTRAINTS'; continue; }
    const fu = t.match(RE.followUp);
    if (fu) {
      flushExample();
      state = 'FOLLOWUP';
      if (fu[1].trim()) followUpLines.push(fu[1].trim());
      continue;
    }

    // --- body by state -----------------------------------------------------
    if (state === 'PROLOGUE') {
      const note = t.match(RE.note);
      if (note) { prologue.push(t); continue; }
      prologue.push(raw);
      continue;
    }

    if (state === 'CONSTRAINTS') {
      if (t === '') continue;
      res.constraintsRaw.push(t);
      const { text: restored, substitutions } = restoreSuperscripts(t);
      res.constraints.push(restored);
      res.superscriptSubs.push(...substitutions);
      continue;
    }

    if (state === 'FOLLOWUP') {
      if (t === '') continue;
      followUpLines.push(t);
      continue;
    }

    // state === 'EXAMPLE'
    if (!example) newExample(null);

    if (RE.imagePlaceholder.test(t)) continue;

    const mi = t.match(RE.input);
    if (mi) {
      // A second `Input:` inside an unnumbered `Examples :` block means GFG's
      // stacked triplet layout: start a new example instead of appending.
      if (example.input.trim() !== '' && example.index === null) {
        const idx = example.index;
        flushExample();
        newExample(idx);
      }
      field = 'input';
      example.input += mi[1];
      continue;
    }
    const mo = t.match(RE.output);
    if (mo) { field = 'output'; example.output += mo[1]; continue; }
    const me = t.match(RE.explanation);
    if (me) { field = 'explanation'; example.explanation += me[1]; continue; }

    // Any other line appends to the open field. Blank lines are buffered, not
    // treated as terminators.
    const target = field ?? 'extra';
    example[target] += (example[target] === '' ? '' : '\n') + raw;
  }

  flushExample();

  res.statement = tidy(prologue.join('\n'));
  res.followUp = followUpLines.length ? followUpLines.join('\n') : null;

  // Superscripts also appear inline in statements ("at most 105 elements").
  if (res.statement) {
    const { text: st, substitutions } = restoreSuperscripts(res.statement);
    res.statement = st;
    res.superscriptSubs.push(...substitutions);
  }

  return res;
}

function tidy(s) {
  if (!s) return '';
  // Collapse blank-line runs, strip leading/trailing blank lines.
  const lines = s.split('\n');
  const out = [];
  let blanks = 0;
  for (const l of lines) {
    if (l.trim() === '') { blanks++; continue; }
    if (blanks > 0 && out.length > 0) out.push('');
    blanks = 0;
    out.push(l.replace(/\s+$/, ''));
  }
  return out.join('\n').trim();
}
