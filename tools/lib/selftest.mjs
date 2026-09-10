// Golden-value assertions measured from the corpus.
//
// The lexer assertions (brace depth, javadoc count) are the highest-value
// tests here: they validate the foundation against grep-verifiable ground
// truth, and every later phase depends on the lexer being right.
//
// When you legitimately add problems, update GOLDEN.files and the per-kind
// counts. If an assertion fails and you did NOT expect it to, that is a
// parser regression, not a stale number.

export const GOLDEN = {
  files: 189,
  javadocSegments: 137,
  noPackage: 2,
  implicitClass: 1,
  filesWithNested: 12,
  trailingContent: 3,
  mainKind: { compact: 176, args: 2, none: 11 },
  packagePathMismatch: 0,
  parseErrors: 0,
  // Phase 2. neetcodeJoins is the count of problems matched to a curated
  // NeetCode video by slug; if this drops, the join or the slugs broke.
  neetcodeJoins: 70,
  blind75: 17,
  neetcode150: 35,
  patternsInUse: 18,
  // Specific files whose classification is load-bearing.
  cases: [
    {
      path: 'src/BinarySearch/SingleElementInSortedarrya.java',
      statementStartsWith: 'You are given a sorted array',
      authorNotesCount: 1,
      examples: 2,
      title: 'Single Element in a Sorted Array',
    },
    {
      // Its javadoc opens "How to approach?" — the scorer must read that as
      // the author's own notes, never as a problem statement. The statement
      // it does show comes from the sidecar, hence statementSource.
      path: 'src/Sorting/QuickSort.java',
      statementSource: 'generated',
      authorNotesCount: 1,
    },
    {
      path: 'src/SlidingWindow/PowerOfKsubArrays.java',
      idsInclude: [3254, 3255],
      examples: 3,
      constraintsInclude: '1 <= nums[i] <= 10^5',
    },
    {
      path: 'src/LeetcodeContest/MaxScoreOfASplit.java',
      difficulty: 'Medium',
      examples: 3,
    },
    {
      path: 'src/GFG/RotateArray.java',
      examples: 3,
    },
    {
      path: 'src/Leetcode/Coupon.java',
      alternateSolutions: 1,
      ids: [3606],
    },
    {
      // Its javadoc is empty (`/**\n *\n */`) and must normalize to null
      // rather than an empty string, so no statement is parsed from source.
      path: 'src/StackAndQueue/TwoStacks.java',
      statementSource: 'generated',
      emptyJavadoc: true,
    },
  ],
};

export function runSelftest(data, golden) {
  const fails = [];
  const ok = [];
  const check = (label, actual, expected) => {
    const pass = JSON.stringify(actual) === JSON.stringify(expected);
    (pass ? ok : fails).push(`${label}: ${JSON.stringify(actual)}${pass ? '' : ` (expected ${JSON.stringify(expected)})`}`);
  };

  const p = data.problems;
  const byPath = Object.fromEntries(p.map((x) => [x.path, x]));
  const countWarn = (code) => data.warnings.filter((w) => w.code === code).length;

  check('files', p.length, golden.files);
  check('no package', p.filter((x) => !x.package).length, golden.noPackage);
  check('implicit class', p.filter((x) => x.implicitClass).length, golden.implicitClass);
  check('files with nested class', p.filter((x) => x.nestedClasses?.length).length, golden.filesWithNested);
  check('trailing content', countWarn('TRAILING_CONTENT'), golden.trailingContent);
  check('package/path mismatch', countWarn('PACKAGE_PATH_MISMATCH'), golden.packagePathMismatch);
  check('parse errors', countWarn('PARSE_ERROR'), golden.parseErrors);
  check('mainKind', {
    compact: p.filter((x) => x.mainKind === 'compact').length,
    args: p.filter((x) => x.mainKind === 'args').length,
    none: p.filter((x) => x.mainKind === 'none').length,
  }, golden.mainKind);

  // --- phase 2: patterns and media -------------------------------------
  check('every problem has a pattern', p.filter((x) => !x.pattern).length, 0);
  check('patterns in use', data.patterns.length, golden.patternsInUse);
  check('neetcode video joins', p.filter((x) => x.video?.source === 'neetcode').length, golden.neetcodeJoins);
  check('blind 75 tagged', p.filter((x) => x.lists?.blind75).length, golden.blind75);
  check('neetcode 150 tagged', p.filter((x) => x.lists?.neetcode150).length, golden.neetcode150);

  // Nothing unverified may ship: a wrong id renders as a broken player.
  const unverified = p.filter((x) => x.video && !x.video.verifiedAt);
  check('videos all oEmbed-verified', unverified.map((x) => x.video.id), []);

  // Every pattern needs a playlist, and each must have passed verification.
  const noPlaylist = data.patterns.filter((x) => !x.playlist).map((x) => x.id);
  check('every pattern has a playlist', noPlaylist, []);
  const unverifiedPl = data.patterns.filter((x) => x.playlist && !x.playlist.verifiedAt).map((x) => x.id);
  check('playlists all verified', unverifiedPl, []);

  for (const c of golden.cases) {
    const rec = byPath[c.path];
    if (!rec) { fails.push(`${c.path}: MISSING from output`); continue; }
    const tag = c.path.replace(/^src\//, '');
    if ('statement' in c) check(`${tag} statement`, rec.statement, c.statement);
    if ('statementSource' in c) check(`${tag} statementSource`, rec.statementSource, c.statementSource);
    if ('emptyJavadoc' in c) {
      check(`${tag} EMPTY_JAVADOC warned`, rec.warnings.some((w) => w.code === 'EMPTY_JAVADOC'), c.emptyJavadoc);
    }
    if (c.statementStartsWith) {
      const got = (rec.statement ?? '').slice(0, c.statementStartsWith.length);
      check(`${tag} statement starts`, got, c.statementStartsWith);
    }
    if ('authorNotesCount' in c) check(`${tag} authorNotes`, rec.authorNotes.length, c.authorNotesCount);
    if ('examples' in c) check(`${tag} examples`, rec.examples.length, c.examples);
    if ('title' in c) check(`${tag} title`, rec.title, c.title);
    if ('difficulty' in c) check(`${tag} difficulty`, rec.difficulty, c.difficulty);
    if ('alternateSolutions' in c) check(`${tag} alternateSolutions`, rec.alternateSolutions.length, c.alternateSolutions);
    if ('ids' in c) check(`${tag} ids`, rec.ids, c.ids);
    if (c.idsInclude) check(`${tag} ids include`, c.idsInclude.every((i) => rec.ids.includes(i)), true);
    if (c.constraintsInclude) check(`${tag} constraint`, rec.constraints.includes(c.constraintsInclude), true);
  }

  console.log(`\nselftest: ${ok.length} passed, ${fails.length} failed`);
  if (fails.length) {
    console.log('-'.repeat(72));
    for (const f of fails) console.log('  FAIL  ' + f);
    return false;
  }
  console.log('-'.repeat(72));
  for (const o of ok) console.log('  ok    ' + o);
  return true;
}
