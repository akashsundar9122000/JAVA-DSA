#!/usr/bin/env node
// Fetch public LeetCode profile stats for the dashboard.
//
//   node tools/fetch-profile.mjs
//
// Needs a username in tools/data/secrets.json (or $LEETCODE_USER). No cookie,
// no password: this is the same data anyone can see on the public profile page.
//
// Be aware this lands in a PUBLIC repo and on a public site. It is already
// public information, but it is published here deliberately, not incidentally.

import { writeFileSync, mkdirSync } from 'node:fs';
import { join } from 'node:path';
import { DATA_DIR, secrets, missing } from './lib/secrets.mjs';

const OUT = join(DATA_DIR, 'profile.json');
const ENDPOINT = 'https://leetcode.com/graphql';

const QUERY = `query profile($u: String!) {
  matchedUser(username: $u) {
    username
    profile { ranking reputation userAvatar realName countryName }
    submitStatsGlobal { acSubmissionNum { difficulty count } }
    submitStats { acSubmissionNum { difficulty count submissions } }
  }
  userContestRanking(username: $u) {
    rating
    attendedContestsCount
    globalRanking
    topPercentage
  }
  allQuestionsCount { difficulty count }
}`;

const main = async () => {
  const { leetcodeUser } = secrets();

  if (!leetcodeUser) {
    missing('LeetCode username', [
      'Add it to tools/data/secrets.json:',
      '  { "leetcodeUser": "your-leetcode-handle" }',
      'or run:  LEETCODE_USER=your-handle node tools/fetch-profile.mjs',
      '',
      'The dashboard simply omits the profile panel until then.',
    ].join('\n'));
  }

  process.stdout.write(`fetching public profile for ${leetcodeUser}\n`);

  const res = await fetch(ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Referer: `https://leetcode.com/u/${leetcodeUser}/`,
      'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36',
    },
    body: JSON.stringify({ query: QUERY, variables: { u: leetcodeUser } }),
  });

  if (!res.ok) {
    console.error(`\nfailed: HTTP ${res.status}. The committed profile.json is untouched.`);
    process.exit(1);
  }

  const json = await res.json();
  const u = json?.data?.matchedUser;

  if (!u) {
    console.error(`\nfailed: no public profile for "${leetcodeUser}".`);
    console.error('Check the handle (it is the name in your profile URL, leetcode.com/u/<handle>).');
    if (json?.errors?.[0]?.message) console.error(`LeetCode said: ${json.errors[0].message}`);
    process.exit(1);
  }

  const byDiff = {};
  for (const row of u.submitStatsGlobal?.acSubmissionNum ?? []) {
    byDiff[row.difficulty.toLowerCase()] = row.count;
  }
  const totals = {};
  for (const row of json?.data?.allQuestionsCount ?? []) {
    totals[row.difficulty.toLowerCase()] = row.count;
  }

  const cr = json?.data?.userContestRanking;
  const out = {
    schemaVersion: 1,
    username: u.username,
    fetchedAt: new Date().toISOString(),
    ranking: u.profile?.ranking ?? null,
    solved: {
      all: byDiff.all ?? null,
      easy: byDiff.easy ?? null,
      medium: byDiff.medium ?? null,
      hard: byDiff.hard ?? null,
    },
    totals: {
      all: totals.all ?? null, easy: totals.easy ?? null,
      medium: totals.medium ?? null, hard: totals.hard ?? null,
    },
    contest: cr ? {
      rating: cr.rating ?? null,
      attended: cr.attendedContestsCount ?? null,
      globalRanking: cr.globalRanking ?? null,
      topPercentage: cr.topPercentage ?? null,
    } : null,
  };

  mkdirSync(DATA_DIR, { recursive: true });
  writeFileSync(OUT, JSON.stringify(out, null, 2) + '\n');

  console.log('wrote tools/data/profile.json');
  console.log(`  ${out.solved.all} solved (${out.solved.easy}E / ${out.solved.medium}M / ${out.solved.hard}H)`);
  if (out.ranking) console.log(`  global rank ${Number(out.ranking).toLocaleString('en')}`);
  if (out.contest?.rating) console.log(`  contest rating ${Math.round(out.contest.rating)} over ${out.contest.attended} contests`);
  console.log('\nNote: this is published to your public site. Delete profile.json to remove it.');
};

main().catch((err) => {
  console.error('\nfailed:', err.message);
  process.exit(1);
});
