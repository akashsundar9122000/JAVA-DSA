#!/usr/bin/env node
// Fetch the public LeetCode problem index and commit a trimmed copy.
//
// Run manually when you add problems:  node tools/fetch-index.mjs
// This is the ONLY script that touches the network. generate.mjs never does,
// so a build always works offline and always produces the same output.
//
// The index gives three things the sources mostly lack:
//   - correct official titles (which fixes every class-name typo for free)
//   - difficulty
//   - id -> slug, so an `//540` tag alone can still produce a real link

import { writeFileSync, mkdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const HERE = dirname(fileURLToPath(import.meta.url));
const OUT = join(HERE, 'data', 'leetcode-index.json');
const SRC = 'https://leetcode.com/api/problems/all/';

const DIFFICULTY = { 1: 'Easy', 2: 'Medium', 3: 'Hard' };

async function main() {
  process.stdout.write(`fetching ${SRC} ...\n`);

  const res = await fetch(SRC, {
    headers: {
      'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36',
      Accept: 'application/json',
    },
  });

  if (!res.ok) {
    console.error(`\nfailed: HTTP ${res.status} ${res.statusText}`);
    console.error('The committed index (if any) is left untouched.');
    process.exit(1);
  }

  const json = await res.json();
  const pairs = json.stat_status_pairs;
  if (!Array.isArray(pairs) || pairs.length === 0) {
    console.error('\nfailed: unexpected payload shape (no stat_status_pairs)');
    process.exit(1);
  }

  // Key on frontend_question_id — the number humans quote and the one his
  // `//540` tags use. `question_id` is an unrelated internal id.
  const byId = {};
  const bySlug = {};
  let paid = 0;

  for (const p of pairs) {
    const id = p.stat?.frontend_question_id;
    const slug = p.stat?.question__title_slug;
    const title = p.stat?.question__title;
    if (!id || !slug) continue;
    const entry = {
      slug,
      title,
      difficulty: DIFFICULTY[p.difficulty?.level] ?? null,
    };
    if (p.paid_only) { entry.paid = true; paid++; }
    byId[String(id)] = entry;
    // slug -> id only; title and difficulty are already in byId.
    bySlug[slug] = id;
  }

  const out = {
    schemaVersion: 1,
    source: SRC,
    fetchedAt: new Date().toISOString(),
    count: Object.keys(byId).length,
    byId,
    bySlug,
  };

  mkdirSync(dirname(OUT), { recursive: true });
  writeFileSync(OUT, JSON.stringify(out) + '\n');

  const kb = (Buffer.byteLength(JSON.stringify(out)) / 1024).toFixed(0);
  console.log(`wrote ${OUT}`);
  console.log(`  ${out.count} problems (${paid} premium), ${kb} KB`);
}

main().catch((err) => {
  console.error('\nfailed:', err.message);
  console.error('The committed index (if any) is left untouched.');
  process.exit(1);
});
