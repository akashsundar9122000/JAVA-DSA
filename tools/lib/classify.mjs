// Header line-comment triage, and the statement-vs-author's-notes scorer.

const COMPANIES = /\b(amazon|microsoft|google|facebook|meta|apple|netflix|adobe|uber|bloomberg|zoho|goldman|flipkart|walmart|oracle|salesforce|paypal|swiggy|zomato)\b/gi;

const RE = {
  url: /(https?:\/\/[^\s)>\]]+)/,
  // `leet\w*` is deliberately sloppy so the corpus typo `//Leetcoe -66` matches.
  idTag: /^leet\w*\s*(?:code)?\s*(?:question|problem)?\s*[-–:]?\s*(\d{1,4}(?:\s*,\s*\d{1,4})*)\s*(?:[-–:]\s*(easy|medium|hard))?\s*(?:[-–:]\s*(.+))?$/i,
  // Digits and commas ONLY. Rejects `//method 2` and `//4/1/26`.
  bareId: /^(\d{1,4}(?:\s*,\s*\d{1,4})*)$/,
  date: /^(\d{1,2})\/(\d{1,2})\/(\d{2,4})$/,
  status: /\b(not yet completed|not completed|incomplete|tle|time limit exceed(?:ed)?|wip|todo)\b/i,
  difficulty: /^(easy|medium|hard)$/i,
  ideBoilerplate: /^TIP\b|<shortcut actionId=|<icon src=/,
};

/**
 * Triage the `//` comment lines of the HEADER region.
 *
 * Position-independent by design: his notes appear both before and after the
 * id/url lines, so ordering is never assumed. `notes` preserves source order.
 */
export function triageHeaderLines(rawLines) {
  const out = {
    ids: [],
    urls: [],
    notes: [],
    companies: [],
    difficulty: null,
    solvedDate: null,
    status: null,
  };

  for (const raw of rawLines) {
    const text = raw.replace(/^\s*\/\/+\s?/, '').trim();
    if (text === '') continue;

    // 1. URL (may sit alongside prose on the same line)
    const mUrl = text.match(RE.url);
    if (mUrl) {
      out.urls.push(mUrl[1]);
      const residue = text.replace(mUrl[1], '').replace(/^[\s\-–:]+|[\s\-–:]+$/g, '');
      if (/[a-z]{3}/i.test(residue)) out.notes.push(residue);
      collectCompanies(residue, out);
      continue;
    }

    // 2. Problem-id tag
    const mTag = text.match(RE.idTag);
    if (mTag) {
      pushIds(out, mTag[1]);
      if (mTag[2]) out.difficulty = titleCase(mTag[2]);
      if (mTag[3]) {
        const residue = mTag[3].trim();
        const mDiff = residue.match(RE.difficulty);
        if (mDiff) out.difficulty = titleCase(mDiff[1]);
        else out.notes.push(residue);
        collectCompanies(residue, out);
      }
      continue;
    }
    const mBare = text.match(RE.bareId);
    if (mBare) { pushIds(out, mBare[1]); continue; }

    // 3. Date stamp — `//4/1/26` is D/M/YY in this corpus (cross-checked
    //    against the envId=2026-01-04 query param on the same file's URL).
    const mDate = text.match(RE.date);
    if (mDate) {
      const [, d, m, y] = mDate;
      const year = y.length === 2 ? 2000 + Number(y) : Number(y);
      out.solvedDate = `${year}-${String(Number(m)).padStart(2, '0')}-${String(Number(d)).padStart(2, '0')}`;
      continue;
    }

    // 4. IDE boilerplate
    if (RE.ideBoilerplate.test(text)) continue;

    // 5. Status
    const mStatus = text.match(RE.status);
    if (mStatus) {
      out.status = /tle|time limit/i.test(mStatus[1]) ? 'tle' : 'incomplete';
      out.notes.push(text);
      collectCompanies(text, out);
      continue;
    }

    // 6. Standalone difficulty
    const mDiff = text.match(RE.difficulty);
    if (mDiff) { out.difficulty = titleCase(mDiff[1]); continue; }

    // 7. Anything else is his own note — the most valuable content here.
    out.notes.push(text);
    collectCompanies(text, out);
  }

  out.ids = [...new Set(out.ids)];
  out.urls = [...new Set(out.urls)];
  out.companies = [...new Set(out.companies)];
  return out;
}

function pushIds(out, group) {
  for (const part of group.split(',')) {
    const n = Number(part.trim());
    if (Number.isInteger(n) && n > 0) out.ids.push(n);
  }
}

function collectCompanies(text, out) {
  for (const m of text.matchAll(COMPANIES)) {
    const name = m[1].toLowerCase();
    out.companies.push(name === 'meta' ? 'Facebook' : titleCase(name));
  }
}

function titleCase(s) {
  return s[0].toUpperCase() + s.slice(1).toLowerCase();
}

// --- statement scoring -----------------------------------------------------

const LEETCODE_OPENER = /^(Given|You are given|You have|You may recall|You will|Return|Implement|Design|Write (a|an) (function|program|algorithm)|There (is|are)|Suppose|Consider|A (string|sentence|permutation|robot|city)|An? \w+ (is|array)|Reverse|Count|Find|Determine|The \w+ (of|sequence|next))/i;

const AUTHOR_OPENER = /^(How to approach|Approach|Intuition|Logic|Steps?|Idea|My approach|Note to self|Explanation of|Formula|Trick)\b/i;

const ALT_SOLUTION = /^(alternate|another way|another sol|alternative|method \d|solution \d|\d+\s?ms solution|optimi[sz]ed)/i;

const CODE_LIKE = [
  /[;{}]\s*$/,
  /^\s*(for|while|if|else|return|int |long |String |List<|Map<|char |boolean |double |void |static |new |ArrayList|HashMap|Arrays\.)/,
  /(==|\+\+|--|\.add\(|\.get\(|\.put\(|\.length|\.size\(\))/,
];

const HIS_VOICE = /\b(we can|we need to|we should|we have to|we are|i\.e,|coz|obviously|untill|skil|gonna|lets|let's|so we|then we|here we)\b/gi;

const LIST_LINE = /^(Case \d|Step \d|\d+[.)]\s|-\s)/;

/**
 * Score a normalized javadoc. >= 4 means "this is a pasted problem statement".
 *
 * His own reasoning must never be presented as a problem statement, so the
 * negatives are weighted to keep author-voice docs out even when they happen
 * to contain an Input:/Output: line.
 */
export function scoreStatement(sectioned, text) {
  const lines = text.split('\n').filter((l) => l.trim() !== '');
  const first = lines[0]?.trim() ?? '';
  let score = 0;
  const why = [];

  const add = (n, label) => { score += n; why.push(`${n > 0 ? '+' : ''}${n} ${label}`); };

  if (/^Examples?\s*\d*\s*:?$/im.test(text)) add(4, 'Example header');
  if (/^Constraints?:?$/im.test(text)) add(3, 'Constraints header');
  if (/^Input\s*[:=]/im.test(text)) add(2, 'Input:');
  if (/^Output\s*[:=]/im.test(text)) add(2, 'Output:');
  if (/^Follow[-\s]?up/im.test(text)) add(2, 'Follow-up');
  if (/^Q\d+\.\s+\S/.test(first)) add(2, 'contest paste');
  if (LEETCODE_OPENER.test(first)) add(2, 'LeetCode opener');
  if (text.length >= 400) add(1, 'length');

  const longSentences = (text.match(/[^.!?]{40,}[.!?]/g) || []).length;
  if (longSentences >= 3) add(1, 'prose');

  if (AUTHOR_OPENER.test(first)) add(-5, 'author opener');

  const codeLike = lines.filter((l) => CODE_LIKE.some((re) => re.test(l))).length;
  if (lines.length > 0 && codeLike / lines.length >= 0.4) add(-4, 'code-like');

  if (lines.some((l) => ALT_SOLUTION.test(l.trim()))) add(-3, 'alt-solution marker');

  const listLines = lines.filter((l) => LIST_LINE.test(l.trim())).length;
  if (lines.length > 0 && listLines / lines.length >= 0.3) add(-2, 'step list');

  const voice = (text.match(HIS_VOICE) || []).length;
  if (voice >= 3) add(-2, 'author voice');

  return { score, why, codeLikeRatio: lines.length ? codeLike / lines.length : 0 };
}

export const STATEMENT_THRESHOLD = 4;
