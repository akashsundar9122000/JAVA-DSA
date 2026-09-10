#!/usr/bin/env node
// Verify that every YouTube id we ship actually resolves.
//
//   node tools/verify-media.mjs                    verify everything referenced
//   node tools/verify-media.mjs --video ID [ID…]   check ids ad hoc
//   node tools/verify-media.mjs --playlist PL… […]  check playlists ad hoc
//
// Why this exists: a wrong video id renders as an embedded player that fails,
// which is worse than no video at all. Two keyless endpoints make every id
// checkable, so nothing unverified ever ships:
//
//   videos    -> youtube.com/oembed  ... 200 with real title+author, 400 if dead
//   playlists -> youtube.com/feeds/videos.xml?playlist_id=…  ... XML with title
//
// The real title and channel that come back are also what we display, so the
// UI shows what YouTube says rather than what we guessed.

import { writeFileSync, mkdirSync } from 'node:fs';
import { join } from 'node:path';
import { DATA_DIR, readJson, sleep } from './lib/secrets.mjs';

const UA = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36';

export async function verifyVideo(id) {
  const url = `https://www.youtube.com/oembed?url=${encodeURIComponent(`https://www.youtube.com/watch?v=${id}`)}&format=json`;
  try {
    const res = await fetch(url, { headers: { 'User-Agent': UA } });
    if (res.status === 200) {
      const j = await res.json();
      return {
        ok: true,
        id,
        title: j.title ?? null,
        channel: j.author_name ?? null,
        channelUrl: j.author_url ?? null,
        thumbnail: j.thumbnail_url ?? null,
        verifiedAt: new Date().toISOString(),
      };
    }
    // 400 = no such video, 401/403 = exists but embedding is disabled.
    return { ok: false, id, status: res.status, reason: res.status === 400 ? 'not found' : 'embedding restricted' };
  } catch (err) {
    return { ok: false, id, status: 0, reason: err.message };
  }
}

export async function verifyPlaylist(id) {
  const url = `https://www.youtube.com/feeds/videos.xml?playlist_id=${encodeURIComponent(id)}`;
  try {
    const res = await fetch(url, { headers: { 'User-Agent': UA } });
    if (res.status !== 200) return { ok: false, id, status: res.status, reason: 'not found' };
    const xml = await res.text();
    const title = xml.match(/<title>([^<]*)<\/title>/)?.[1] ?? null;
    const author = xml.match(/<author>\s*<name>([^<]*)<\/name>/)?.[1] ?? null;
    const items = (xml.match(/<yt:videoId>/g) ?? []).length;
    const firstVideo = xml.match(/<yt:videoId>([^<]+)<\/yt:videoId>/)?.[1] ?? null;
    if (!title) return { ok: false, id, status: 200, reason: 'no title in feed' };
    return { ok: true, id, title, channel: author, sampleItems: items, firstVideo, verifiedAt: new Date().toISOString() };
  } catch (err) {
    return { ok: false, id, status: 0, reason: err.message };
  }
}

const decode = (s) => (s ?? '').replace(/&amp;/g, '&').replace(/&quot;/g, '"').replace(/&#39;/g, "'").replace(/&lt;/g, '<').replace(/&gt;/g, '>');

async function adHoc(kind, ids) {
  let bad = 0;
  for (const id of ids) {
    const r = kind === 'video' ? await verifyVideo(id) : await verifyPlaylist(id);
    if (r.ok) {
      console.log(`  OK   ${id}  ${decode(r.title)}${r.channel ? `  — ${decode(r.channel)}` : ''}${r.sampleItems !== undefined ? `  (${r.sampleItems} recent items)` : ''}`);
    } else {
      bad++;
      console.log(`  DEAD ${id}  ${r.reason} (HTTP ${r.status})`);
    }
    await sleep(150);
  }
  console.log(`\n${ids.length - bad}/${ids.length} verified`);
  if (bad) process.exitCode = 1;
}

const main = async () => {
  const argv = process.argv.slice(2);

  if (argv[0] === '--video') return adHoc('video', argv.slice(1));
  if (argv[0] === '--playlist') return adHoc('playlist', argv.slice(1));

  // Full pass: everything any data file references.
  const neetcode = readJson('neetcode.json', { bySlug: {} });
  const curated = readJson('curated.json', { playlists: {}, conceptVideos: {} });
  const videos = readJson('videos.json', { byId: {}, meta: {} });
  videos.meta ??= {};

  const videoIds = new Set();
  for (const rec of Object.values(neetcode.bySlug ?? {})) if (rec.video) videoIds.add(rec.video);
  for (const rec of Object.values(videos.byId ?? {})) if (rec.videoId) videoIds.add(rec.videoId);
  for (const rec of Object.values(curated.conceptVideos ?? {})) if (rec.videoId) videoIds.add(rec.videoId);

  // Only the NeetCode videos our own deck actually uses — verifying all 450
  // would be 450 requests for videos we never render.
  let used = videoIds;
  try {
    const deck = JSON.parse(await import('node:fs').then((fs) => fs.promises.readFile(join(DATA_DIR, '..', '..', 'docs', 'data.json'), 'utf8')));
    const referenced = new Set();
    for (const p of deck.problems) {
      if (p.video?.id) referenced.add(p.video.id);
      if (p.conceptVideo?.id) referenced.add(p.conceptVideo.id);
    }
    for (const rec of Object.values(curated.conceptVideos ?? {})) if (rec.videoId) referenced.add(rec.videoId);
    if (referenced.size) used = referenced;
  } catch { /* fall back to everything referenced by the data files */ }

  const playlistIds = Object.values(curated.playlists ?? {}).map((p) => p.playlistId).filter(Boolean);

  console.log(`verifying ${used.size} videos and ${playlistIds.length} playlists\n`);

  const dead = [];
  let n = 0;
  for (const id of [...used].sort()) {
    const r = await verifyVideo(id);
    n++;
    if (r.ok) {
      videos.meta[id] = {
        title: decode(r.title), channel: decode(r.channel),
        thumbnail: r.thumbnail, verifiedAt: r.verifiedAt,
      };
      process.stdout.write(`  [${String(n).padStart(3)}/${used.size}] ${id}  ${decode(r.title).slice(0, 58)}\n`);
    } else {
      dead.push(r);
      delete videos.meta[id];
      process.stdout.write(`  [${String(n).padStart(3)}/${used.size}] ${id}  DEAD — ${r.reason}\n`);
    }
    await sleep(120);
  }

  for (const [patternId, pl] of Object.entries(curated.playlists ?? {})) {
    if (!pl.playlistId) continue;
    const r = await verifyPlaylist(pl.playlistId);
    if (r.ok) {
      pl.verifiedTitle = decode(r.title);
      pl.verifiedChannel = decode(r.channel);
      pl.posterVideoId = r.firstVideo ?? null;
      pl.verifiedAt = r.verifiedAt;
      console.log(`  playlist ${patternId.padEnd(22)} OK   ${decode(r.title).slice(0, 50)}`);
    } else {
      pl.verifiedTitle = null;
      pl.verifiedAt = null;
      dead.push({ ...r, patternId });
      console.log(`  playlist ${patternId.padEnd(22)} DEAD ${r.reason}`);
    }
    await sleep(150);
  }

  videos.verifiedAt = new Date().toISOString();
  videos.deadIds = dead.filter((d) => !d.patternId).map((d) => d.id);

  mkdirSync(DATA_DIR, { recursive: true });
  writeFileSync(join(DATA_DIR, 'videos.json'), JSON.stringify(videos, null, 2) + '\n');
  writeFileSync(join(DATA_DIR, 'curated.json'), JSON.stringify(curated, null, 2) + '\n');

  console.log(`\n${used.size - dead.filter((d) => !d.patternId).length}/${used.size} videos verified`);
  console.log(`metadata written for ${Object.keys(videos.meta).length} videos`);
  if (dead.length) {
    console.log(`\n${dead.length} dead reference(s) — these will render as a search button, not a broken embed:`);
    for (const d of dead) console.log(`  ${d.patternId ? `playlist ${d.patternId} ` : ''}${d.id}: ${d.reason}`);
  }
};

if (import.meta.url === `file://${process.argv[1]}`) {
  main().catch((err) => { console.error('\nfailed:', err.message); process.exit(1); });
}
