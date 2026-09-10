#!/usr/bin/env node
// Find an explanation video for each problem the NeetCode dataset does not cover.
//
//   node tools/fetch-videos.mjs [--limit N] [--force] [--dry]
//
// Needs a YouTube Data API v3 key in tools/data/secrets.json (or
// $YOUTUBE_API_KEY). Create one at console.cloud.google.com: new project ->
// enable "YouTube Data API v3" -> Credentials -> API key. No billing card.
//
// Quota: search.list costs 100 units of a 10,000/day allowance, so ~100
// searches a day. This run needs one search per uncovered problem (about 58
// today), which fits comfortably. The script prints its running spend and
// stops before it would exceed the daily budget.
//
// Nothing is trusted just because the API returned it: results are restricted
// to the approved channels, scored against the official LeetCode title, and
// every accepted id is re-checked through oEmbed before being written.

import { writeFileSync, readFileSync, mkdirSync } from 'node:fs';
import { join } from 'node:path';
import { DATA_DIR, ROOT, readJson, secrets, missing, sleep } from './lib/secrets.mjs';
import { verifyVideo } from './verify-media.mjs';

const OUT = join(DATA_DIR, 'videos.json');
const API = 'https://www.googleapis.com/youtube/v3';
const SEARCH_COST = 100;
const DAILY_QUOTA = 10000;
const SAFETY_MARGIN = 500;   // leave room for the playlist harvester

const argv = process.argv.slice(2);
const LIMIT = argv.includes('--limit') ? Number(argv[argv.indexOf('--limit') + 1]) : Infinity;
const FORCE = argv.includes('--force');
const DRY = argv.includes('--dry');

/** Title similarity: token overlap, which is robust to the "- Leetcode 33 - Python" suffixes. */
function score(candidateTitle, problemTitle, channelName) {
  const norm = (s) => s.toLowerCase().replace(/[^a-z0-9 ]+/g, ' ').split(/\s+/).filter(Boolean);
  const want = new Set(norm(problemTitle));
  const got = norm(candidateTitle);
  if (!want.size) return 0;

  let hits = 0;
  for (const w of got) if (want.has(w)) hits++;
  // Recall against the problem title matters more than the candidate's length.
  const recall = [...want].filter((w) => got.includes(w)).length / want.size;
  let s = recall * 100 + hits;

  // Prefer titles that look like a problem walkthrough.
  if (/leetcode|leet code/i.test(candidateTitle)) s += 12;
  if (/solution|explained|explanation|approach/i.test(candidateTitle)) s += 4;
  // Penalise contest recaps and playlists-of-everything.
  if (/contest|weekly|biweekly|marathon|live stream|shorts/i.test(candidateTitle)) s -= 25;
  if (channelName === 'NeetCode') s += 3;
  return s;
}

const main = async () => {
  const { youtubeApiKey } = secrets();

  if (!youtubeApiKey) {
    missing('YouTube API key', [
      'Add it to tools/data/secrets.json:',
      '  { "youtubeApiKey": "AIza..." }',
      '',
      'Get one free at console.cloud.google.com:',
      '  new project -> enable "YouTube Data API v3" -> Credentials -> API key',
      '',
      'Until then the site ships the 70 NeetCode-matched videos plus the',
      'curated pattern playlists, and uncovered problems show a search button.',
    ].join('\n'));
  }

  let deck;
  try {
    deck = JSON.parse(readFileSync(join(ROOT, 'docs', 'data.json'), 'utf8'));
  } catch {
    console.error('failed: docs/data.json not found. Run `npm run build` first.');
    process.exit(1);
  }

  const curated = readJson('curated.json', { channels: {} });
  const approved = new Map();          // channelId -> display name
  const approvedNames = new Set(Object.values(curated.channels ?? {}).map((c) => c.name));

  const store = readJson('videos.json', { schemaVersion: 1, byId: {}, meta: {} });
  store.byId ??= {}; store.meta ??= {};

  // Resolve channel handles to ids once (1 unit each).
  let spent = 0;
  for (const [key, ch] of Object.entries(curated.channels ?? {})) {
    if (ch.channelId) { approved.set(ch.channelId, ch.name); continue; }
    const url = `${API}/channels?part=id&forHandle=${encodeURIComponent(ch.handle)}&key=${youtubeApiKey}`;
    const res = await fetch(url);
    spent += 1;
    if (!res.ok) {
      const body = await res.text();
      console.error(`\nfailed resolving ${ch.handle}: HTTP ${res.status}`);
      if (res.status === 403) console.error('403 usually means the key is restricted or the API is not enabled on the project.');
      console.error(body.slice(0, 300));
      process.exit(1);
    }
    const j = await res.json();
    const id = j.items?.[0]?.id;
    if (id) { approved.set(id, ch.name); console.log(`resolved ${ch.handle} -> ${id}`); }
    else console.log(`could not resolve ${ch.handle} (skipping that channel)`);
    await sleep(120);
  }

  // Problems needing a video: has a LeetCode slug, no video yet.
  const todo = deck.problems
    .filter((p) => p.kind !== 'scratch' && p.slug && p.site === 'leetcode.com')
    .filter((p) => !p.video || p.video.source === 'search')
    .filter((p) => FORCE || !store.byId[p.id])
    .slice(0, LIMIT);

  console.log(`\n${todo.length} problems need a searched video`);
  console.log(`budget: ${DAILY_QUOTA - SAFETY_MARGIN} units usable, ${SEARCH_COST}/search -> ${Math.floor((DAILY_QUOTA - SAFETY_MARGIN) / SEARCH_COST)} searches\n`);
  if (DRY) { for (const p of todo) console.log(`  would search: ${p.title}`); return; }
  if (!todo.length) { console.log('nothing to do.'); return; }

  let found = 0;
  const skipped = [];

  for (let i = 0; i < todo.length; i++) {
    const p = todo[i];

    if (spent + SEARCH_COST > DAILY_QUOTA - SAFETY_MARGIN) {
      console.log(`\nstopping: daily quota nearly spent (${spent} units). Re-run tomorrow to continue.`);
      break;
    }

    const q = `${p.title} leetcode`;
    const url = `${API}/search?part=snippet&type=video&maxResults=10&q=${encodeURIComponent(q)}&key=${youtubeApiKey}`;
    const res = await fetch(url);
    spent += SEARCH_COST;

    if (res.status === 403) {
      const body = await res.text();
      console.error(`\nquota exhausted or key rejected (HTTP 403) after ${spent} units.`);
      console.error(body.slice(0, 300));
      break;
    }
    if (!res.ok) { skipped.push({ p, why: `HTTP ${res.status}` }); continue; }

    const j = await res.json();
    const cands = (j.items ?? [])
      .map((it) => ({
        id: it.id?.videoId,
        title: it.snippet?.title ?? '',
        channel: it.snippet?.channelTitle ?? '',
        channelId: it.snippet?.channelId ?? '',
      }))
      .filter((c) => c.id)
      // Only the four channels he approved. This is the main quality gate.
      .filter((c) => approved.has(c.channelId) || approvedNames.has(c.channel))
      .map((c) => ({ ...c, score: score(c.title, p.title, c.channel) }))
      .sort((a, b) => b.score - a.score);

    const best = cands[0];
    // A low score means the top hit is not really this problem.
    if (!best || best.score < 60) {
      skipped.push({ p, why: best ? `best score ${Math.round(best.score)} too low ("${best.title.slice(0, 44)}")` : 'no result from an approved channel' });
      process.stdout.write(`  [${String(i + 1).padStart(3)}/${todo.length}] ${p.title.slice(0, 42).padEnd(42)} — no confident match\n`);
      await sleep(150);
      continue;
    }

    // Trust nothing until oEmbed confirms it and hands back the real title.
    const v = await verifyVideo(best.id);
    if (!v.ok) {
      skipped.push({ p, why: `failed verification: ${v.reason}` });
      await sleep(150);
      continue;
    }

    store.byId[p.id] = {
      videoId: best.id,
      title: v.title,
      channel: v.channel,
      matchScore: Math.round(best.score),
      query: q,
      foundAt: new Date().toISOString(),
    };
    store.meta[best.id] = { title: v.title, channel: v.channel, thumbnail: v.thumbnail, verifiedAt: v.verifiedAt };
    found++;
    process.stdout.write(`  [${String(i + 1).padStart(3)}/${todo.length}] ${p.title.slice(0, 42).padEnd(42)} -> ${v.channel}: ${String(v.title).slice(0, 38)}\n`);

    if (i % 10 === 9) {
      mkdirSync(DATA_DIR, { recursive: true });
      writeFileSync(OUT, JSON.stringify(store, null, 2) + '\n');
    }
    await sleep(200);
  }

  store.fetchedAt = new Date().toISOString();
  mkdirSync(DATA_DIR, { recursive: true });
  writeFileSync(OUT, JSON.stringify(store, null, 2) + '\n');

  console.log(`\nwrote tools/data/videos.json`);
  console.log(`  ${found} new videos, ${Object.keys(store.byId).length} total searched`);
  console.log(`  quota spent this run: ${spent} units`);
  if (skipped.length) {
    console.log(`\n  ${skipped.length} left without a video (they show a search button, not a broken embed):`);
    for (const s of skipped.slice(0, 12)) console.log(`    ${s.p.title}: ${s.why}`);
    if (skipped.length > 12) console.log(`    …and ${skipped.length - 12} more`);
  }
  console.log('\nnext: npm run build');
};

main().catch((err) => {
  console.error('\nfailed:', err.message);
  process.exit(1);
});
