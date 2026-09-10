#!/usr/bin/env node
// Upgrade pattern playlists from "a full course that covers this" to a
// playlist dedicated to the pattern, where one exists.
//
//   node tools/fetch-playlists.mjs [--dry]
//
// Needs a YouTube Data API v3 key. Cheap: playlists.list costs 1 unit per page
// of 50, so listing every playlist on all four channels costs under 20 units.
//
// It never overwrites a hand-picked 'dedicated' entry in curated.json — those
// were chosen deliberately. It only proposes replacements for patterns still
// sitting on a 'course' fallback, and writes the real title and channel it saw.

import { writeFileSync, mkdirSync } from 'node:fs';
import { join } from 'node:path';
import { DATA_DIR, readJson, secrets, missing, sleep } from './lib/secrets.mjs';
import { verifyPlaylist } from './verify-media.mjs';
import { PATTERNS } from './lib/patterns.mjs';

const API = 'https://www.googleapis.com/youtube/v3';
const DRY = process.argv.includes('--dry');

/**
 * Words that indicate a playlist is about a given pattern. Deliberately
 * conservative: a wrong match here would put the wrong course on a pattern,
 * which is worse than leaving the honest 'full course' fallback in place.
 */
const PATTERN_WORDS = {
  'arrays-hashing': ['array', 'hashing', 'hash map'],
  'two-pointers': ['two pointer', 'twopointer'],
  'sliding-window': ['sliding window'],
  'prefix-sum': ['prefix sum'],
  'stack': ['stack'],
  'queue': ['queue', 'deque'],
  'binary-search': ['binary search'],
  'sorting': ['sorting', 'sort algorithm'],
  'linked-list': ['linked list', 'linkedlist'],
  'trees': ['tree', 'binary tree', 'bst'],
  'tries': ['trie'],
  'heap': ['heap', 'priority queue'],
  'recursion': ['recursion', 'recursive'],
  'backtracking': ['backtracking'],
  'graphs': ['graph'],
  'advanced-graphs': ['advanced graph', 'dijkstra', 'shortest path', 'union find', 'spanning tree'],
  'dp-1d': ['dynamic programming', ' dp ', 'dp series'],
  'dp-2d': ['dynamic programming', 'dp on grids', 'dp on strings'],
  'greedy': ['greedy'],
  'intervals': ['interval', 'merge interval'],
  'math-geometry': ['math', 'number theory', 'geometry', 'bit math'],
  'bit-manipulation': ['bit manipulation', 'bitwise'],
  'data-structures': ['data structure'],
};

/** Reject playlists that are shorts, live streams, or contest recaps. */
const NOISE = /shorts|live|contest|weekly|biweekly|podcast|vlog|announcement|interview experience/i;

const main = async () => {
  const { youtubeApiKey } = secrets();

  if (!youtubeApiKey) {
    missing('YouTube API key', [
      'Add it to tools/data/secrets.json:',
      '  { "youtubeApiKey": "AIza..." }',
      '',
      'The curated pattern playlists already in tools/data/curated.json are all',
      'verified and working — this script only tries to upgrade the ones marked',
      '"course" to a playlist dedicated to that pattern.',
    ].join('\n'));
  }

  const curated = readJson('curated.json', null);
  if (!curated) { console.error('failed: tools/data/curated.json missing.'); process.exit(1); }

  let spent = 0;
  const all = [];

  for (const [key, ch] of Object.entries(curated.channels ?? {})) {
    let channelId = ch.channelId;

    if (!channelId) {
      const res = await fetch(`${API}/channels?part=id&forHandle=${encodeURIComponent(ch.handle)}&key=${youtubeApiKey}`);
      spent += 1;
      if (!res.ok) { console.log(`  could not resolve ${ch.handle} (HTTP ${res.status}), skipping`); continue; }
      const j = await res.json();
      channelId = j.items?.[0]?.id;
      if (!channelId) { console.log(`  no channel for ${ch.handle}, skipping`); continue; }
      ch.channelId = channelId;   // cache it back into curated.json
    }

    let pageToken = '';
    let count = 0;
    do {
      const url = `${API}/playlists?part=snippet,contentDetails&channelId=${channelId}&maxResults=50&key=${youtubeApiKey}${pageToken ? `&pageToken=${pageToken}` : ''}`;
      const res = await fetch(url);
      spent += 1;
      if (!res.ok) { console.log(`  ${ch.name}: HTTP ${res.status} listing playlists`); break; }
      const j = await res.json();
      for (const it of j.items ?? []) {
        all.push({
          playlistId: it.id,
          title: it.snippet?.title ?? '',
          channel: ch.name,
          items: it.contentDetails?.itemCount ?? 0,
        });
        count++;
      }
      pageToken = j.nextPageToken ?? '';
      await sleep(150);
    } while (pageToken);

    console.log(`${ch.name.padEnd(18)} ${count} playlists`);
  }

  console.log(`\n${all.length} playlists across all channels · ${spent} quota units\n`);

  const proposals = [];
  for (const def of PATTERNS) {
    const current = curated.playlists?.[def.id];
    if (current?.kind === 'dedicated') continue;      // hand-picked, leave alone

    const words = PATTERN_WORDS[def.id] ?? [];
    if (!words.length) continue;

    const hits = all
      .filter((pl) => !NOISE.test(pl.title))
      .filter((pl) => pl.items >= 5)
      .map((pl) => {
        const t = ` ${pl.title.toLowerCase()} `;
        const matched = words.filter((w) => t.includes(w));
        // Longer, more specific phrases count for more than single words.
        const strength = matched.reduce((n, w) => n + (w.includes(' ') ? 3 : 1), 0);
        return { ...pl, strength, matched };
      })
      .filter((pl) => pl.strength > 0)
      .sort((a, b) => b.strength - a.strength || b.items - a.items);

    if (hits.length) proposals.push({ def, best: hits[0], runnerUp: hits[1] ?? null });
  }

  if (!proposals.length) {
    console.log('No dedicated playlist found for any pattern still on a course fallback.');
    console.log('The existing curated entries stay as they are.');
    return;
  }

  console.log('proposed upgrades (course fallback -> dedicated playlist):');
  for (const { def, best } of proposals) {
    console.log(`  ${def.name.padEnd(24)} ${best.channel} · ${best.title.slice(0, 52)} (${best.items} videos)`);
  }

  if (DRY) { console.log('\n--dry: nothing written.'); return; }

  // Verify each before committing it, same gate as everything else.
  let applied = 0;
  for (const { def, best } of proposals) {
    const v = await verifyPlaylist(best.playlistId);
    if (!v.ok) { console.log(`  ${def.name}: failed verification (${v.reason}), keeping fallback`); continue; }
    curated.playlists[def.id] = {
      playlistId: best.playlistId,
      kind: 'dedicated',
      channel: best.channel,
      why: `${best.items}-video series on ${def.name.toLowerCase()} from ${best.channel}.`,
      verifiedTitle: v.title,
      verifiedChannel: v.channel,
      posterVideoId: v.firstVideo ?? null,
      verifiedAt: v.verifiedAt,
      autoMatched: true,
    };
    applied++;
    await sleep(150);
  }

  mkdirSync(DATA_DIR, { recursive: true });
  writeFileSync(join(DATA_DIR, 'curated.json'), JSON.stringify(curated, null, 2) + '\n');

  console.log(`\nwrote tools/data/curated.json — ${applied} pattern(s) upgraded, ${spent} quota units spent`);
  console.log('Entries marked autoMatched:true were chosen by title match; skim them and');
  console.log('correct any that look wrong, then run: npm run build');
};

main().catch((err) => {
  console.error('\nfailed:', err.message);
  process.exit(1);
});
