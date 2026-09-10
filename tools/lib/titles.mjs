// Display titles.
//
// The official LeetCode title, looked up by slug or id, is always preferred:
// it is correct by construction and fixes every class-name typo for free
// (SingleElementInSortedarrya -> "Single Element in a Sorted Array").
// The class-name splitter is the fallback, and is also always computed so
// `--audit-titles` can show both side by side.

// Longest-match first, so DNA beats D+N+A.
const ACRONYMS = [
  'AVL', 'DNA', 'BST', 'GFG', 'DFS', 'BFS', 'LCA', 'XOR', 'API', 'FIFO',
  'LIFO', 'LRU', 'RPN', 'KMP', 'LIS', 'LCS', 'GCD', 'LCM', 'DP',
];
// Deliberately NOT acronyms: `LL`, which must reach TOKEN_FIXUPS to become
// "Linked List". Acronym tokens bypass the fixup table.

const SMALL_WORDS = new Set([
  'a', 'an', 'the', 'of', 'in', 'on', 'to', 'for', 'and', 'or', 'with',
  'at', 'by', 'from', 'is', 'are', 'as', 'into', 'after', 'per',
]);

const ROMAN = { '2': 'II', '3': 'III', '4': 'IV' };

/**
 * Whole-class-name fixups, applied before splitting. Needed where a typo has
 * no internal case boundary to split on (`Defusebomb`, `Squareroot`).
 */
const NAME_FIXUPS = {
  singleelementinsortedarrya: 'Single Element In Sorted Array',
  defusebomb: 'Defuse Bomb',
  squareroot: 'Square Root',
  sumofdigits: 'Sum Of Digits',
  cielingproblem: 'Ceiling Problem',
  inbuildexamples: 'Built In Examples',
  sortedmatrixleetcode: 'Sorted Matrix',
  minvaluetopositivestepby: 'Min Value To Get Positive Step By Step Sum',
  noofniceSubarrays: 'Number Of Nice Subarrays',
};

/** Per-token fixups, applied after splitting. */
const TOKEN_FIXUPS = {
  heighest: 'Highest',
  paranthesis: 'Parentheses',
  paranthesses: 'Parentheses',
  longes: 'Longest',
  ceasar: 'Caesar',
  cicular: 'Circular',
  cieling: 'Ceiling',
  sortedarrya: 'Sorted Array',
  arrya: 'Array',
  atlease: 'At Least',
  atleast: 'At Least',
  subarr: 'Subarray',
  arr: 'Array',
  noof: 'Number Of',
  no: 'Number',
  inbuild: 'Built In',
  ll: 'Linked List',
  substring: 'Substring',
  subsequence: 'Subsequence',
  ksub: 'K Sub',
  biotonic: 'Bitonic',
  reversepolish: 'Reverse Polish',
  maxscore: 'Max Score',
  stepby: 'Step By',
  divisors: 'Divisors',
  paranthesises: 'Parentheses',
};

/** Tokens stripped when they are just repo bookkeeping. */
const STRIP_TOKENS = new Set(['leetcode', 'leetcod', 'gfg']);

/**
 * Split a PascalCase class name into display words.
 */
export function splitClassName(className, siblingNames = []) {
  const lower = className.toLowerCase();
  if (NAME_FIXUPS[lower]) return NAME_FIXUPS[lower];

  // 1. Carve out acronyms first so they are never split.
  const parts = [];
  let i = 0;
  while (i < className.length) {
    const upperRest = className.slice(i).toUpperCase();
    const hit = ACRONYMS.find((a) => upperRest.startsWith(a) && className.slice(i, i + a.length) === a);
    if (hit) { parts.push({ acronym: true, text: hit }); i += hit.length; continue; }
    parts.push({ acronym: false, text: className[i] });
    i++;
  }

  // 2. Split the non-acronym runs on case and letter/digit boundaries.
  const tokens = [];
  let buf = '';
  const flush = () => { if (buf) { tokens.push({ acronym: false, text: buf }); buf = ''; } };

  for (let k = 0; k < parts.length; k++) {
    const p = parts[k];
    if (p.acronym) { flush(); tokens.push(p); continue; }
    const ch = p.text;
    const prev = buf[buf.length - 1];
    const nextPart = parts[k + 1];
    const next = nextPart && !nextPart.acronym ? nextPart.text : null;

    const isUpper = /[A-Z]/.test(ch);
    const isDigit = /[0-9]/.test(ch);
    const prevIsLower = prev && /[a-z]/.test(prev);
    const prevIsDigit = prev && /[0-9]/.test(prev);
    const prevIsUpper = prev && /[A-Z]/.test(prev);

    if (
      (isUpper && prevIsLower) ||                       // fooBar
      (isUpper && prevIsUpper && next && /[a-z]/.test(next)) || // HTTPServer
      (isDigit && !prevIsDigit && prev) ||              // Subset2
      (!isDigit && prevIsDigit)                         // 1Bit
    ) flush();

    buf += ch;
  }
  flush();

  // 3. Fixups, stripping, casing.
  const words = [];
  for (const t of tokens) {
    if (t.acronym) { words.push(t.text); continue; }
    const key = t.text.toLowerCase();
    if (STRIP_TOKENS.has(key)) continue;
    if (TOKEN_FIXUPS[key]) { words.push(...TOKEN_FIXUPS[key].split(' ')); continue; }
    words.push(t.text[0].toUpperCase() + t.text.slice(1));
  }

  // 4. A trailing 2/3 becomes a roman numeral only when a sibling file shares
  //    the stem, which is what makes it a genuine sequel rather than a count.
  const last = words[words.length - 1];
  if (last && ROMAN[last]) {
    const stem = className.replace(/[234]$/, '');
    if (siblingNames.includes(stem)) words[words.length - 1] = ROMAN[last];
  }

  return applySmallWords(words).join(' ');
}

function applySmallWords(words) {
  return words.map((w, idx) => {
    if (idx === 0) return w;
    if (ACRONYMS.includes(w)) return w;
    // A lone capital is a variable name ("Remove A In String"), not a word.
    if (w.length === 1) return w;
    const lower = w.toLowerCase();
    return SMALL_WORDS.has(lower) ? lower : w;
  });
}

/** Slug -> display title, used only when the LeetCode index has no entry. */
export function titleFromSlug(slug) {
  const words = slug.split('-')
    // GeeksforGeeks appends a numeric problem id to its slugs
    // ("count-pairs-with-given-sum-150253", "key-pair5616"). It is an id, not
    // a word, so strip it rather than printing it as part of the title.
    .filter((w) => !/^\d{4,}$/.test(w))
    .map((w) => w.replace(/(?<=[a-z])\d{4,}$/, ''))
    .filter(Boolean)
    .map((w) => {
    const up = w.toUpperCase();
    if (ACRONYMS.includes(up)) return up;
    if (/^(i{1,3}|iv|v|vi{1,3})$/i.test(w)) return w.toUpperCase();
    return w ? w[0].toUpperCase() + w.slice(1) : w;
  });
  return applySmallWords(words).join(' ');
}

/**
 * Resolve the display title.
 *
 * Cascade, first hit wins, and `titleSource` records which one fired so the
 * audit is honest about what was derived versus what is authoritative.
 */
export function resolveTitle({ override, pasteTitle, slug, ids, className, fileBase, slugIndex, siblingNames }) {
  const derivedTitle = splitClassName(className, siblingNames);

  if (override) return { title: override, titleSource: 'override', derivedTitle, officialTitle: null };

  // Official title by slug, then by id.
  let official = null;
  if (slug && slugIndex?.bySlug?.[slug] !== undefined) {
    official = slugIndex.byId[String(slugIndex.bySlug[slug])] ?? null;
  }
  if (!official) {
    for (const id of ids ?? []) {
      const hit = slugIndex?.byId?.[String(id)];
      if (hit) { official = hit; break; }
    }
  }
  if (official?.title) {
    return { title: official.title, titleSource: 'leetcode', derivedTitle, officialTitle: official.title };
  }

  if (pasteTitle) return { title: pasteTitle, titleSource: 'paste', derivedTitle, officialTitle: null };
  if (slug) return { title: titleFromSlug(slug), titleSource: 'slug', derivedTitle, officialTitle: null };
  if (className && className !== fileBase) return { title: derivedTitle, titleSource: 'classname', derivedTitle, officialTitle: null };
  return { title: derivedTitle || fileBase, titleSource: 'classname', derivedTitle, officialTitle: null };
}
