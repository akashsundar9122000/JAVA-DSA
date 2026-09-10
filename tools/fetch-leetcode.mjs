#!/usr/bin/env node
// Enrich each problem from LeetCode's public GraphQL endpoint.
//
//   node tools/fetch-leetcode.mjs [--force] [--limit N]
//
// Pulls the things the pasted javadocs never contained: real topic tags (which
// drive pattern assignment), official hints, similar problems, and acceptance
// rate. No authentication — this is all public problem metadata.
//
// The endpoint rate-limits bursts, so requests are sequential and spaced. The
// cache is merged, not replaced, so a interrupted run resumes where it stopped
// and a failed slug never destroys a good cached record.

import { writeFileSync, readFileSync, mkdirSync } from 'node:fs';
import { join } from 'node:path';
import { DATA_DIR, ROOT, readJson, sleep } from './lib/secrets.mjs';

const OUT = join(DATA_DIR, 'leetcode-problems.json');
const ENDPOINT = 'https://leetcode.com/graphql';
const SPACING_MS = 350;

const QUERY = `query problem($slug: String!) {
  question(titleSlug: $slug) {
    questionFrontendId
    title
    titleSlug
    difficulty
    isPaidOnly
    likes
    dislikes
    stats
    hints
    topicTags { name slug }
    similarQuestionList { titleSlug title difficulty }
  }
}`;

const argv = process.argv.slice(2);
const FORCE = argv.includes('--force');
const LIMIT = argv.includes('--limit') ? Number(argv[argv.indexOf('--limit') + 1]) : Infinity;

async function fetchOne(slug) {
  try {
    const res = await fetch(ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        // LeetCode rejects requests without a plausible referer.
        Referer: `https://leetcode.com/problems/${slug}/`,
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36',
      },
      body: JSON.stringify({ query: QUERY, variables: { slug } }),
    });

    if (res.status === 429) return { retry: true };
    if (!res.ok) return { error: `HTTP ${res.status}` };

    const json = await res.json();
    const q = json?.data?.question;
    if (!q) return { error: json?.errors?.[0]?.message ?? 'no question in response' };

    let acRate = null;
    try { acRate = JSON.parse(q.stats).acRate; } catch { /* stats is optional */ }

    return {
      record: {
        id: Number(q.questionFrontendId) || null,
        title: q.title,
        slug: q.titleSlug,
        difficulty: q.difficulty,
        paidOnly: !!q.isPaidOnly,
        acRate,
        likes: q.likes ?? null,
        dislikes: q.dislikes ?? null,
        topicTags: (q.topicTags ?? []).map((t) => t.name),
        hints: q.hints ?? [],
        similar: (q.similarQuestionList ?? []).map((s) => ({
          slug: s.titleSlug, title: s.title, difficulty: s.difficulty,
        })),
        fetchedAt: new Date().toISOString(),
      },
    };
  } catch (err) {
    return { error: err.message };
  }
}

const main = async () => {
  // Slug list comes from the built deck, which is committed.
  let deck;
  try {
    deck = JSON.parse(readFileSync(join(ROOT, 'docs', 'data.json'), 'utf8'));
  } catch {
    console.error('failed: docs/data.json not found. Run `npm run build` first.');
    process.exit(1);
  }

  const cache = readJson('leetcode-problems.json', { schemaVersion: 1, bySlug: {} });
  cache.bySlug ??= {};

  const slugs = [...new Set(
    deck.problems
      .filter((p) => p.slug && p.site === 'leetcode.com')
      .map((p) => p.slug),
  )].sort();

  const todo = slugs.filter((s) => FORCE || !cache.bySlug[s]).slice(0, LIMIT);

  console.log(`${slugs.length} LeetCode slugs in the deck`);
  console.log(`${slugs.length - todo.length} already cached, fetching ${todo.length}`);
  if (!todo.length) { console.log('nothing to do.'); return; }
  console.log(`spacing ${SPACING_MS}ms to stay under the rate limit\n`);

  let ok = 0;
  const failed = [];

  for (let i = 0; i < todo.length; i++) {
    const slug = todo[i];
    let res = await fetchOne(slug);

    if (res.retry) {
      // Backing off once is almost always enough.
      process.stdout.write('  rate limited, backing off 5s\n');
      await sleep(5000);
      res = await fetchOne(slug);
    }

    if (res.record) {
      cache.bySlug[slug] = res.record;
      ok++;
      const tags = res.record.topicTags.slice(0, 3).join(', ');
      process.stdout.write(`  [${String(i + 1).padStart(3)}/${todo.length}] ${slug.padEnd(52).slice(0, 52)} ${tags}\n`);
    } else {
      failed.push({ slug, error: res.error });
      process.stdout.write(`  [${String(i + 1).padStart(3)}/${todo.length}] ${slug.padEnd(52).slice(0, 52)} FAILED: ${res.error}\n`);
    }

    // Persist as we go, so an interrupted run keeps its progress.
    if (i % 10 === 9 || i === todo.length - 1) {
      cache.fetchedAt = new Date().toISOString();
      mkdirSync(DATA_DIR, { recursive: true });
      writeFileSync(OUT, JSON.stringify(cache, null, 2) + '\n');
    }

    if (i < todo.length - 1) await sleep(SPACING_MS);
  }

  const all = Object.values(cache.bySlug);
  console.log(`\nwrote tools/data/leetcode-problems.json`);
  console.log(`  ${ok} fetched this run, ${all.length} cached total`);
  console.log(`  with tags: ${all.filter((r) => r.topicTags.length).length}`);
  console.log(`  with hints: ${all.filter((r) => r.hints.length).length}`);
  console.log(`  with similar: ${all.filter((r) => r.similar.length).length}`);
  if (failed.length) {
    console.log(`\n  ${failed.length} failed (re-run to retry just these):`);
    for (const f of failed.slice(0, 10)) console.log(`    ${f.slug}: ${f.error}`);
  }
};

main().catch((err) => {
  console.error('\nfailed:', err.message);
  process.exit(1);
});
