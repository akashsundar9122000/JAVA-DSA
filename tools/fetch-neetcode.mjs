#!/usr/bin/env node
// Download NeetCode's public problem dataset.
//
//   node tools/fetch-neetcode.mjs
//
// It is a curated map of LeetCode problem -> explanation video id, plus a
// pattern name and Blind-75 / NeetCode-150 membership. That makes it the single
// highest-value free source here: it supplies exact, human-curated video ids for
// the problems it covers, so nothing has to be guessed or searched for them.
//
// No API key. One request.

import { writeFileSync, mkdirSync } from 'node:fs';
import { join } from 'node:path';
import { DATA_DIR, get } from './lib/secrets.mjs';

const SRC = 'https://raw.githubusercontent.com/neetcode-gh/leetcode/main/.problemSiteData.json';
const OUT = join(DATA_DIR, 'neetcode.json');

const main = async () => {
  process.stdout.write(`fetching ${SRC}\n`);
  const res = await get(SRC);

  if (!res.ok || !Array.isArray(res.body)) {
    console.error(`\nfailed: HTTP ${res.status}${res.error ? ` (${res.error})` : ''}`);
    console.error('The committed copy (if any) is left untouched.');
    process.exit(1);
  }

  const entries = res.body;
  const bySlug = {};
  const patterns = new Map();
  let withVideo = 0;

  for (const e of entries) {
    // `link` is the LeetCode slug with a trailing slash, e.g. "two-sum/".
    const slug = String(e.link ?? '').replace(/^\/+|\/+$/g, '');
    if (!slug) continue;

    const rec = {
      title: e.problem ?? null,
      pattern: e.pattern ?? null,
      difficulty: e.difficulty ?? null,
      blind75: !!e.blind75,
      neetcode150: !!e.neetcode150,
    };
    // A few entries carry no video; record the problem anyway for the
    // Blind-75 / NeetCode-150 checklists, which do not depend on video.
    if (e.video) { rec.video = e.video; withVideo++; }

    bySlug[slug] = rec;
    if (rec.pattern) patterns.set(rec.pattern, (patterns.get(rec.pattern) ?? 0) + 1);
  }

  const out = {
    schemaVersion: 1,
    source: SRC,
    fetchedAt: new Date().toISOString(),
    counts: {
      problems: Object.keys(bySlug).length,
      withVideo,
      blind75: entries.filter((e) => e.blind75).length,
      neetcode150: entries.filter((e) => e.neetcode150).length,
    },
    patterns: [...patterns.entries()].sort((a, b) => b[1] - a[1]).map(([name, count]) => ({ name, count })),
    bySlug,
  };

  mkdirSync(DATA_DIR, { recursive: true });
  writeFileSync(OUT, JSON.stringify(out, null, 2) + '\n');

  console.log(`wrote tools/data/neetcode.json`);
  console.log(`  ${out.counts.problems} problems, ${withVideo} with a video`);
  console.log(`  ${out.counts.blind75} on Blind 75, ${out.counts.neetcode150} on NeetCode 150`);
  console.log(`  ${out.patterns.length} patterns: ${out.patterns.slice(0, 6).map((p) => p.name).join(', ')}…`);
};

main().catch((err) => {
  console.error('\nfailed:', err.message);
  process.exit(1);
});
