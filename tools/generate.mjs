#!/usr/bin/env node
// Build docs/data.json from src/**/*.java.
//
//   node tools/generate.mjs [--pretty] [--no-timestamp] [--audit-titles]
//                           [--stubs] [--file <path>] [--selftest] [--check]
//
// Never touches the network and never writes to src/. The .java files are the
// single source of truth; everything here is derived.

import { readFileSync, writeFileSync, readdirSync, statSync, mkdirSync, existsSync } from 'node:fs';
import { join, dirname, basename, relative, sep } from 'node:path';
import { fileURLToPath } from 'node:url';
import { execFileSync } from 'node:child_process';

import { lex } from './lib/lexer.mjs';
import { analyze, partitionComments } from './lib/structure.mjs';
import { normalizeDoc, sectionDoc } from './lib/javadoc.mjs';
import { triageHeaderLines, scoreStatement, STATEMENT_THRESHOLD } from './lib/classify.mjs';
import { classifyUrl, resolveCanonical } from './lib/urls.mjs';
import { resolveTitle, splitClassName } from './lib/titles.mjs';
import { GOLDEN, runSelftest } from './lib/selftest.mjs';

const HERE = dirname(fileURLToPath(import.meta.url));
const ROOT = dirname(HERE);
const SRC = join(ROOT, 'src');
const OUT_DIR = join(ROOT, 'docs');
const OUT = join(OUT_DIR, 'data.json');

const CATEGORY_NAMES = {
  'BinarySearch': 'Binary Search',
  'BitManipulation': 'Bit Manipulation',
  'CompanyQuestions': 'Company Questions',
  'CompanyQuestions/Microsoft': 'Microsoft',
  'GFG': 'GeeksforGeeks',
  'Graph': 'Graphs',
  'Heap': 'Heap',
  'Leetcode': 'LeetCode Mixed',
  'Leetcode/DailyChallenge': 'Daily Challenge',
  'LeetcodeContest': 'Contests',
  'LinkedList': 'Linked List',
  'LinkedList/Basic': 'Linked List Basics',
  'PrefixSum': 'Prefix Sum',
  'Recursion': 'Recursion',
  'Recursion/BackTracking': 'Backtracking',
  'SlidingWindow': 'Sliding Window',
  'Sorting': 'Sorting',
  'StackAndQueue': 'Stack & Queue',
  'Trees': 'Trees',
  'TwoPointer': 'Two Pointers',
  '': 'Scratch',
};

const WARN = {
  NO_PACKAGE: 'no package declaration',
  PACKAGE_PATH_MISMATCH: 'package does not mirror directory path',
  NO_ID: 'no problem id tag',
  NO_URL: 'no problem URL',
  NO_STATEMENT: 'no problem statement',
  EMPTY_JAVADOC: 'javadoc present but empty',
  NO_MAIN: 'no main method',
  MULTI_ID: 'multiple problem ids in one file',
  LOW_TITLE_CONFIDENCE: 'title derived from class name',
  ID_URL_SLUG_MISMATCH: 'id tag disagrees with URL slug',
  UNKNOWN_HOST: 'unrecognized problem host',
  SUPERSCRIPT_GUESS: 'superscript restored in constraints',
  TRAILING_CONTENT: 'content after the class closing brace',
  DUPLICATE_TITLE: 'title shared with another file',
  ORPHAN_OVERRIDE: 'override key matches no file',
  PARSE_ERROR: 'parser threw; record holds source only',
};

// --- CLI -------------------------------------------------------------------

const argv = process.argv.slice(2);
const flag = (name) => argv.includes(name);
const opt = (name) => {
  const i = argv.indexOf(name);
  return i === -1 ? null : argv[i + 1];
};

const OPTS = {
  pretty: flag('--pretty'),
  noTimestamp: flag('--no-timestamp'),
  auditTitles: flag('--audit-titles'),
  stubs: flag('--stubs'),
  one: opt('--file'),
  selftest: flag('--selftest'),
  check: flag('--check'),
};

// --- helpers ---------------------------------------------------------------

function walk(dir, out = []) {
  for (const entry of readdirSync(dir).sort()) {
    const p = join(dir, entry);
    if (statSync(p).isDirectory()) walk(p, out);
    else if (entry.endsWith('.java')) out.push(p);
  }
  return out;
}

function readJson(p, fallback) {
  try { return JSON.parse(readFileSync(p, 'utf8')); } catch { return fallback; }
}

function git(...args) {
  try { return execFileSync('git', args, { cwd: ROOT, encoding: 'utf8' }).trim(); } catch { return null; }
}

function slugify(s) {
  return s
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/[^A-Za-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '')
    .toLowerCase();
}

/** Codepoint compare — locale-independent so output is machine-independent. */
function cmp(a, b) { return a < b ? -1 : a > b ? 1 : 0; }

// --- per-file parse --------------------------------------------------------

function parseFile(absPath, ctx) {
  const rel = relative(ROOT, absPath).split(sep).join('/');
  const fileBase = basename(absPath, '.java');
  const categoryPath = relative(SRC, dirname(absPath)).split(sep).join('/').replace(/^\.$/, '');

  const rawSource = readFileSync(absPath, 'utf8');
  const source = rawSource.replace(/^﻿/, '').replace(/\r\n?/g, '\n');
  const warnings = [];
  const addWarn = (code, extra) => warnings.push(extra ? { code, detail: extra } : { code });

  const base = {
    id: `${slugify(categoryPath) || 'root'}/${slugify(fileBase)}`,
    path: rel,
    category: slugify(categoryPath) || 'root',
    categoryPath,
    className: fileBase,
    source,
    lineCount: source.split('\n').length,
    warnings,
  };

  try {
    const lexed = lex(source);
    if (lexed.endDepth !== 0) addWarn('PARSE_ERROR', `brace depth ${lexed.endDepth} at EOF`);

    const a = analyze(source, lexed, fileBase);
    const regions = partitionComments(lexed.segments, a);

    if (!a.packageName) addWarn('NO_PACKAGE');
    else {
      const expected = categoryPath.split('/').filter(Boolean).join('.');
      if (a.packageName !== expected) addWarn('PACKAGE_PATH_MISMATCH', `${a.packageName} != ${expected}`);
    }
    if (a.mainKind === 'none') addWarn('NO_MAIN');

    // --- header line comments ---------------------------------------------
    const headerLineSegs = regions.header.filter((s) => s.type === 'line');
    const headerLines = headerLineSegs.flatMap((s) => s.lines ?? [s.text]);
    // Promote a preamble line that carries real metadata (rare).
    const preambleLines = regions.preamble
      .filter((s) => s.type === 'line')
      .flatMap((s) => s.lines ?? [s.text])
      .filter((l) => /https?:\/\/|leet/i.test(l));

    const tri = triageHeaderLines([...preambleLines, ...headerLines]);

    // Merge the sidecar BEFORE resolution, so an overridden id or url also
    // feeds the official title and difficulty lookups rather than only the
    // link. Sources on disk are never modified — this is the whole point of
    // keeping the sidecar separate.
    const ov = ctx.overrides[rel] ?? {};
    const overriddenIds = [].concat(ov.ids ?? []).filter((n) => Number.isInteger(n));
    const overriddenUrls = [].concat(ov.url ?? ov.urls ?? []).filter(Boolean);
    if (overriddenIds.length) tri.ids = [...new Set([...tri.ids, ...overriddenIds])];
    if (overriddenUrls.length) tri.urls = [...new Set([...tri.urls, ...overriddenUrls])];

    // --- header javadocs: statement vs his own notes -----------------------
    const headerDocs = [];
    for (const seg of regions.header.filter((s) => s.type === 'javadoc')) {
      const norm = normalizeDoc(seg.text);
      if (!norm) { addWarn('EMPTY_JAVADOC', `line ${seg.startLine}`); continue; }
      const sectioned = sectionDoc(norm.text);
      const scored = scoreStatement(sectioned, norm.text);
      headerDocs.push({ seg, norm, sectioned, scored });
    }

    let statementDoc = null;
    const authorNotes = [];
    const candidates = headerDocs.filter((d) => d.scored.score >= STATEMENT_THRESHOLD);
    if (candidates.length) {
      statementDoc = candidates.reduce((best, d) => (d.scored.score > best.scored.score ? d : best));
    }
    for (const d of headerDocs) {
      if (d === statementDoc) continue;
      authorNotes.push({ text: d.norm.text, line: d.seg.startLine, source: 'javadoc' });
    }

    // --- trailing region ---------------------------------------------------
    const alternateSolutions = [];
    if (regions.trailing.length) addWarn('TRAILING_CONTENT');
    for (const seg of regions.trailing) {
      if (seg.type === 'javadoc' || seg.type === 'block') {
        const norm = normalizeDoc(seg.text.replace(/^\s*\/\*+/, '/**'));
        if (!norm) continue;
        const scored = scoreStatement(null, norm.text);
        if (scored.codeLikeRatio >= 0.4) {
          alternateSolutions.push({ label: 'Alternate solution', code: norm.text, line: seg.startLine });
        } else {
          authorNotes.push({ text: norm.text, line: seg.startLine, source: 'trailing' });
        }
      } else {
        const text = (seg.lines ?? [seg.text]).map((l) => l.replace(/^\s*\/\/+\s?/, '')).join('\n').trim();
        if (text) authorNotes.push({ text, line: seg.startLine, source: 'trailing' });
      }
    }

    // --- member-region URLs ------------------------------------------------
    const memberUrls = [];
    for (const seg of regions.member) {
      for (const m of seg.text.matchAll(/(https?:\/\/[^\s*)>\]]+)/g)) memberUrls.push(m[1]);
    }

    // --- URLs --------------------------------------------------------------
    const allRawUrls = [...new Set([...tri.urls, ...memberUrls])];
    const classified = allRawUrls.map(classifyUrl);
    for (const c of classified) if (c.role === 'unknown') addWarn('UNKNOWN_HOST', c.host);

    const sectioned = statementDoc?.sectioned ?? null;
    const ids = tri.ids;
    if (ids.length === 0) addWarn('NO_ID');
    if (ids.length > 1) addWarn('MULTI_ID', ids.join(', '));

    const resolved = resolveCanonical(classified, ids, ctx.slugIndex.byId);
    if (!resolved.hasCanonicalUrl) addWarn('NO_URL');

    // Cross-check: a mistyped id tag shows up as a slug disagreement.
    if (ids.length && resolved.slug && ctx.slugIndex.bySlug) {
      const urlId = ctx.slugIndex.bySlug[resolved.slug];
      if (urlId !== undefined && !ids.includes(urlId)) {
        addWarn('ID_URL_SLUG_MISMATCH', `tag ${ids.join(',')} vs slug id ${urlId}`);
      }
    }

    // --- title -------------------------------------------------------------
    const override = ov;
    const titleInfo = resolveTitle({
      override: override.title,
      pasteTitle: sectioned?.pasteTitle ?? null,
      slug: resolved.slug,
      ids,
      className: a.className,
      fileBase,
      slugIndex: ctx.slugIndex,
      siblingNames: ctx.siblingsByDir[categoryPath] ?? [],
    });
    if (titleInfo.titleSource === 'classname') addWarn('LOW_TITLE_CONFIDENCE');

    // --- difficulty --------------------------------------------------------
    let difficulty = override.difficulty ?? tri.difficulty ?? sectioned?.difficulty ?? null;
    if (!difficulty) {
      for (const id of ids) {
        const hit = ctx.slugIndex.byId?.[String(id)];
        if (hit?.difficulty) { difficulty = hit.difficulty; break; }
      }
    }
    if (!difficulty && resolved.slug && ctx.slugIndex.bySlug?.[resolved.slug] !== undefined) {
      difficulty = ctx.slugIndex.byId?.[String(ctx.slugIndex.bySlug[resolved.slug])]?.difficulty ?? null;
    }

    // --- statement ---------------------------------------------------------
    let statement = sectioned?.statement || null;
    let statementSource = statement ? 'source' : null;
    if (!statement && override.statement) { statement = override.statement; statementSource = 'generated'; }
    if (!statement) addWarn('NO_STATEMENT');

    if (sectioned?.superscriptSubs?.length) addWarn('SUPERSCRIPT_GUESS', sectioned.superscriptSubs.map((s) => `${s.from}->${s.to}`).join(' '));

    // --- notes -------------------------------------------------------------
    const notes = [...tri.notes];
    if (override.notes) notes.push(...[].concat(override.notes));

    // --- kind --------------------------------------------------------------
    const hasProblem = ids.length > 0 || resolved.hasCanonicalUrl;
    let kind;
    if (!a.packageName && /^(Test)?Main$/.test(fileBase) && !hasProblem) kind = 'scratch';
    else if (hasProblem) kind = 'problem';
    else kind = 'concept';

    // --- body offsets ------------------------------------------------------
    const headerEndLine = Math.max(a.classBodyOpenLine, a.firstMemberLine - 1);
    const bodyStartLine = a.firstMemberLine;
    const bodyEndLine = a.classBodyCloseLine;

    return {
      ...base,
      className: a.className,
      kind,
      title: titleInfo.title,
      titleSource: titleInfo.titleSource,
      derivedTitle: titleInfo.derivedTitle,
      officialTitle: titleInfo.officialTitle,

      ids,
      relatedIds: ids.slice(1),
      slug: resolved.slug,
      canonicalUrl: resolved.canonicalUrl,
      hasCanonicalUrl: resolved.hasCanonicalUrl,
      urlSource: resolved.urlSource,
      site: resolved.site,
      urls: classified.map((c) => ({ raw: c.raw, canonical: c.canonical, host: c.host, role: c.role, slug: c.slug, source: overriddenUrls.includes(c.raw) ? 'generated' : 'source' })),
      idsSource: overriddenIds.length && tri.ids.every((i) => overriddenIds.includes(i)) ? 'generated' : 'source',
      submissionIds: classified.map((c) => c.submissionId).filter(Boolean),
      contest: classified.find((c) => c.contest)?.contest ?? null,
      dailyChallengeDate: classified.map((c) => c.envId).find((e) => e && /^\d{4}-\d{2}-\d{2}$/.test(e)) ?? null,

      difficulty,
      companies: [...new Set([...(tri.companies), ...(categoryPath.startsWith('CompanyQuestions/') ? [categoryPath.split('/')[1]] : []), ...[].concat(override.companies ?? [])])],
      solvedDate: tri.solvedDate,
      status: tri.status,
      contestQuestionNo: sectioned?.contestQuestionNo ?? null,
      contestPoints: sectioned?.contestPoints ?? null,

      notes,
      authorNotes,
      alternateSolutions,

      statement,
      statementSource,
      statementConfidence: statementDoc?.scored.score ?? null,
      statementRepairs: statementDoc?.norm.repairs ?? [],
      examples: sectioned?.examples ?? [],
      constraints: sectioned?.constraints ?? [],
      // Only carried when a superscript restore actually changed a line, so a
      // guess stays auditable without paying for 189 identical copies.
      constraintsRaw: JSON.stringify(sectioned?.constraints ?? []) === JSON.stringify(sectioned?.constraintsRaw ?? [])
        ? null : sectioned.constraintsRaw,
      followUp: sectioned?.followUp ?? null,

      package: a.packageName,
      imports: a.imports,
      mainKind: a.mainKind,
      implicitClass: a.implicitClass,
      nestedClasses: a.nestedClasses,
      signatureLines: a.signatureLines,
      headerEndLine,
      bodyStartLine,
      bodyEndLine,

      teaches: override.teaches ?? null,
      complexity: override.complexity ?? null,
      overridden: Object.keys(override),
    };
  } catch (err) {
    // Never drop a file. A bare code listing beats a missing entry.
    addWarn('PARSE_ERROR', `${err.message}`);
    return {
      ...base,
      kind: 'unparsed',
      title: splitClassName(fileBase, []),
      titleSource: 'filename',
      derivedTitle: splitClassName(fileBase, []),
      ids: [], urls: [], canonicalUrl: null, hasCanonicalUrl: false,
      notes: [], authorNotes: [], alternateSolutions: [], examples: [],
      constraints: [], constraintsRaw: [], signatureLines: [], nestedClasses: [],
      statement: null, difficulty: null, companies: [],
    };
  }
}

// --- main ------------------------------------------------------------------

function main() {
  const files = walk(SRC);

  const slugIndex = readJson(join(HERE, 'data', 'leetcode-index.json'), { byId: {}, bySlug: {} });
  const overrides = readJson(join(HERE, 'data', 'overrides.json'), {});

  // Sibling class names per directory, so `Permutation2` can become
  // "Permutation II" only when `Permutation` actually exists next to it.
  const siblingsByDir = {};
  for (const f of files) {
    const dir = relative(SRC, dirname(f)).split(sep).join('/').replace(/^\.$/, '');
    (siblingsByDir[dir] ??= []).push(basename(f, '.java'));
  }

  const ctx = { slugIndex, overrides, siblingsByDir };

  if (OPTS.one) {
    const abs = OPTS.one.startsWith('/') ? OPTS.one : join(ROOT, OPTS.one);
    console.log(JSON.stringify(parseFile(abs, ctx), null, 2));
    return;
  }

  const problems = files.map((f) => parseFile(f, ctx));
  problems.sort((a, b) => cmp(a.categoryPath, b.categoryPath) || cmp(a.className, b.className));

  // Duplicate titles are expected and legitimate: BubbleSort exists in both
  // Recursion and Sorting (recursive vs iterative), and there are two
  // SudokuSolver files. Flag them and attach a qualifier so search results
  // stay distinguishable — never merge or rename.
  const titleCounts = {};
  for (const p of problems) titleCounts[p.title] = (titleCounts[p.title] ?? 0) + 1;
  for (const p of problems) {
    if (titleCounts[p.title] > 1) {
      p.warnings.push({ code: 'DUPLICATE_TITLE' });
      p.titleQualifier = CATEGORY_NAMES[p.categoryPath] ?? p.categoryPath ?? null;
    } else {
      p.titleQualifier = null;
    }
  }

  // Orphan overrides catch a rename silently dropping manual metadata.
  const known = new Set(problems.map((p) => p.path));
  const orphans = Object.keys(overrides).filter((k) => !k.startsWith('_') && !known.has(k));

  // --- categories ---------------------------------------------------------
  const catPaths = [...new Set(problems.map((p) => p.categoryPath))].sort(cmp);
  const categories = catPaths.map((path) => {
    const mine = problems.filter((p) => p.categoryPath === path);
    const parentPath = path.includes('/') ? path.slice(0, path.lastIndexOf('/')) : null;
    return {
      id: slugify(path) || 'root',
      path,
      package: path.split('/').filter(Boolean).join('.') || null,
      name: CATEGORY_NAMES[path] ?? splitClassName(path.split('/').pop() ?? 'Root', []),
      parent: parentPath && catPaths.includes(parentPath) ? slugify(parentPath) : null,
      depth: path === '' ? 0 : path.split('/').length,
      counts: {
        files: mine.length,
        problems: mine.filter((p) => p.kind === 'problem').length,
        concepts: mine.filter((p) => p.kind === 'concept').length,
        withStatement: mine.filter((p) => p.statement).length,
        withExamples: mine.filter((p) => p.examples.length).length,
        withConstraints: mine.filter((p) => p.constraints.length).length,
        withUrl: mine.filter((p) => p.hasCanonicalUrl).length,
        withId: mine.filter((p) => p.ids.length).length,
        withNotes: mine.filter((p) => p.notes.length || p.authorNotes.length).length,
      },
    };
  });

  // --- facets -------------------------------------------------------------
  const tally = (fn) => {
    const m = {};
    for (const p of problems) for (const v of [].concat(fn(p) ?? [])) if (v) m[v] = (m[v] ?? 0) + 1;
    return Object.entries(m).sort((a, b) => b[1] - a[1] || cmp(a[0], b[0])).map(([name, count]) => ({ name, count }));
  };

  const warningCounts = {};
  for (const p of problems) for (const w of p.warnings) warningCounts[w.code] = (warningCounts[w.code] ?? 0) + 1;
  if (orphans.length) warningCounts.ORPHAN_OVERRIDE = orphans.length;

  const commit = git('rev-parse', 'HEAD');
  const data = {
    schemaVersion: 1,
    meta: {
      generator: 'tools/generate.mjs',
      ...(OPTS.noTimestamp ? {} : { generatedAt: new Date().toISOString() }),
      git: {
        commit,
        shortCommit: commit ? commit.slice(0, 7) : null,
        branch: git('rev-parse', '--abbrev-ref', 'HEAD'),
        commitSubject: git('log', '-1', '--pretty=%s'),
        commitDate: git('log', '-1', '--pretty=%cI'),
      },
      counts: {
        files: problems.length,
        problems: problems.filter((p) => p.kind === 'problem').length,
        concepts: problems.filter((p) => p.kind === 'concept').length,
        scratch: problems.filter((p) => p.kind === 'scratch').length,
        unparsed: problems.filter((p) => p.kind === 'unparsed').length,
        withId: problems.filter((p) => p.ids.length).length,
        withCanonicalUrl: problems.filter((p) => p.hasCanonicalUrl).length,
        withStatement: problems.filter((p) => p.statement).length,
        withExamples: problems.filter((p) => p.examples.length).length,
        withConstraints: problems.filter((p) => p.constraints.length).length,
        withDifficulty: problems.filter((p) => p.difficulty).length,
        withNotes: problems.filter((p) => p.notes.length || p.authorNotes.length).length,
        totalSourceLines: problems.reduce((n, p) => n + p.lineCount, 0),
      },
      slugIndex: { present: Object.keys(slugIndex.byId ?? {}).length > 0, entries: Object.keys(slugIndex.byId ?? {}).length, fetchedAt: slugIndex.fetchedAt ?? null },
      warningCounts,
    },
    categories,
    facets: {
      difficulties: tally((p) => p.difficulty),
      companies: tally((p) => p.companies),
      sites: tally((p) => p.site),
      kinds: tally((p) => p.kind),
    },
    problems,
    warnings: problems.flatMap((p) => p.warnings.map((w) => ({ ...w, path: p.path, message: WARN[w.code] ?? w.code })))
      .concat(orphans.map((k) => ({ code: 'ORPHAN_OVERRIDE', path: k, message: WARN.ORPHAN_OVERRIDE }))),
  };

  if (OPTS.selftest) { process.exit(runSelftest(data, GOLDEN) ? 0 : 1); }
  if (OPTS.auditTitles) { auditTitles(problems); return; }
  if (OPTS.stubs) { printStubs(problems); return; }

  // derivedTitle/officialTitle exist for --audit-titles only; the browser
  // never reads them, so drop them from the shipped payload.
  for (const p of data.problems) { delete p.derivedTitle; delete p.officialTitle; }

  mkdirSync(OUT_DIR, { recursive: true });
  const json = OPTS.pretty ? JSON.stringify(data, null, 2) : JSON.stringify(data);
  writeFileSync(OUT, json + '\n');

  stampServiceWorker(commit, json.length);
  writeBundle(data, json);

  report(data, json);

  if (OPTS.check) {
    const baseline = readJson(join(HERE, 'data', 'expected-warnings.json'), null);
    if (baseline) {
      const grew = Object.entries(warningCounts).filter(([code, n]) => n > (baseline[code] ?? 0));
      if (grew.length) {
        console.error('\n--check FAILED: warnings grew beyond baseline');
        for (const [code, n] of grew) console.error(`  ${code}: ${n} > ${baseline[code] ?? 0}`);
        process.exit(1);
      }
      console.log('\n--check OK: no warning code grew beyond baseline');
    } else {
      writeFileSync(join(HERE, 'data', 'expected-warnings.json'), JSON.stringify(warningCounts, null, 2) + '\n');
      console.log('\n--check: wrote initial baseline to tools/data/expected-warnings.json');
    }
  }
}

/**
 * Rewrite the service worker's VERSION so a redeploy invalidates the old
 * cache. Keyed on the commit AND the payload length, so regenerating without
 * committing still busts the cache during local testing.
 */
function stampServiceWorker(commit, bytes) {
  const swPath = join(OUT_DIR, 'sw.js');
  if (!existsSync(swPath)) return;
  const version = `${(commit ?? 'local').slice(0, 7)}-${bytes.toString(36)}`;
  const src = readFileSync(swPath, 'utf8');
  const next = src.replace(/^const VERSION = '[^']*';$/m, `const VERSION = '${version}';`);
  if (next !== src) writeFileSync(swPath, next);
}

/**
 * Emit a single self-contained HTML file with the CSS, JS and deck inlined.
 * This is what gets published as an Artifact, so there is a working cloud URL
 * before GitHub Pages is switched on. Same code, one file.
 */
function writeBundle(data, json) {
  const read = (f) => readFileSync(join(OUT_DIR, f), 'utf8');
  let html;
  try { html = read('index.html'); } catch { return; }

  const css = read('style.css');
  const js = read('app.js');

  // The bundle has no service worker and no separate icon files, so drop the
  // tags that would 404 and inline everything else.
  html = html
    .replace(/^<!doctype html>\s*<html[^>]*>\s*<head>[\s\S]*?<title>/i, '<title>')
    .replace(/<\/head>\s*<body>/i, '')
    .replace(/<\/body>\s*<\/html>\s*$/i, '')
    .replace(/<link rel="manifest"[^>]*>\s*/i, '')
    .replace(/<link rel="apple-touch-icon"[^>]*>\s*/i, '')
    .replace(/<link rel="icon"[^>]*>\s*/i, '')
    .replace(/<link rel="stylesheet" href="style\.css">/i, `<style>\n${css}\n</style>`)
    .replace(/<script src="app\.js" type="module"><\/script>/i,
      `<script>window.__DECK_DATA__=${json};</script>\n<script type="module">\n${js}\n</script>`);

  writeFileSync(join(OUT_DIR, 'bundle.html'), html);
}

// --- reporting -------------------------------------------------------------

function report(data, json) {
  const { counts } = data.meta;
  const bytes = Buffer.byteLength(json);

  const byCode = {};
  for (const w of data.warnings) (byCode[w.code] ??= []).push(w.path);

  console.log('\nwarnings by code');
  console.log('-'.repeat(72));
  const codes = Object.entries(byCode).sort((a, b) => b[1].length - a[1].length);
  if (codes.length === 0) console.log('  none');
  for (const [code, paths] of codes) {
    console.log(`  ${code.padEnd(24)} ${String(paths.length).padStart(3)}  ${WARN[code] ?? ''}`);
  }

  console.log('\ncoverage by category');
  console.log('-'.repeat(72));
  console.log('  category                    files   id  url stmt   ex cons note   %');
  const row = (name, c) => {
    const pct = c.files === 0 ? 0 : Math.round(
      ((c.withId + c.withUrl + c.withStatement + c.withExamples + c.withConstraints) / (c.files * 5)) * 100,
    );
    console.log(
      `  ${name.slice(0, 26).padEnd(26)} ${String(c.files).padStart(5)} ${String(c.withId).padStart(4)} ${String(c.withUrl).padStart(4)} ${String(c.withStatement).padStart(4)} ${String(c.withExamples).padStart(4)} ${String(c.withConstraints).padStart(4)} ${String(c.withNotes).padStart(4)} ${String(pct).padStart(3)}%`,
    );
  };
  for (const c of data.categories) row(c.path || '(root)', c.counts);
  console.log('  ' + '-'.repeat(70));
  row('TOTAL', {
    files: counts.files, withId: counts.withId, withUrl: counts.withCanonicalUrl,
    withStatement: counts.withStatement, withExamples: counts.withExamples,
    withConstraints: counts.withConstraints, withNotes: counts.withNotes,
  });

  console.log('\nsummary');
  console.log('-'.repeat(72));
  console.log(`  files          ${counts.files}   (problem ${counts.problems}, concept ${counts.concepts}, scratch ${counts.scratch}, unparsed ${counts.unparsed})`);
  console.log(`  difficulty     ${counts.withDifficulty}/${counts.files} resolved`);
  console.log(`  data.json      ${(bytes / 1024).toFixed(0)} KB${OPTS.pretty ? ' (pretty)' : ' minified'}`);
  if (bytes > 1.5 * 1024 * 1024) console.log('  !! over 1.5 MB — time to shard source out of the index');
  else if (bytes > 1024 * 1024) console.log('  !  over 1 MB — consider sharding soon');
  console.log(`  wrote          ${relative(ROOT, OUT)}`);
  const bundlePath = join(OUT_DIR, 'bundle.html');
  if (existsSync(bundlePath)) {
    console.log(`  bundle         docs/bundle.html  ${(statSync(bundlePath).size / 1024).toFixed(0)} KB single-file`);
  }
}

function auditTitles(problems) {
  console.log('path | class | derived | official | chosen | source');
  console.log('-'.repeat(120));
  for (const p of problems) {
    const flagged = p.titleSource === 'classname' ? ' <-- review' : '';
    console.log(
      [p.path.replace(/^src\//, ''), p.className, p.derivedTitle, p.officialTitle ?? '', p.title, p.titleSource + flagged].join(' | '),
    );
  }
  const n = problems.filter((p) => p.titleSource === 'classname').length;
  console.log(`\n${n}/${problems.length} titles derived from the class name (marked "review")`);
}

function printStubs(problems) {
  const need = problems.filter((p) => !p.statement || !p.hasCanonicalUrl || p.titleSource === 'classname');
  const out = {};
  for (const p of need) {
    const e = {};
    if (p.titleSource === 'classname') e.title = p.title;
    if (!p.hasCanonicalUrl) e.url = '';
    if (!p.statement) e.statement = '';
    e.teaches = '';
    out[p.path] = e;
  }
  console.log(JSON.stringify(out, null, 2));
  console.error(`\n${need.length} files need attention. Paste into tools/data/overrides.json.`);
}

main();
