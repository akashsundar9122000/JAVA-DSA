/* Java DSA Revision Deck
 * Self-contained: no imports, no CDN at runtime. Everything the page needs is
 * here or in data.json, which is what lets the service worker serve it offline.
 */

/* ======================================================== java highlight ==
 * Tokenized per line, carrying block-comment / text-block state across lines,
 * so every rendered line is self-contained and can carry a line number.
 */

const JAVA_KEYWORDS = new Set(('abstract assert boolean break byte case catch char class const continue default do ' +
  'double else enum extends final finally float for goto if implements import instanceof int interface long native ' +
  'new package private protected public return short static strictfp super switch synchronized this throw throws ' +
  'transient try void volatile while var record sealed permits yield true false null').split(' '));

const JAVA_SOFT = new Set(('String Integer Long Double Float Boolean Character Byte Short Object Math System ' +
  'List ArrayList LinkedList Map HashMap TreeMap LinkedHashMap Set HashSet TreeSet LinkedHashSet Queue Deque ' +
  'ArrayDeque PriorityQueue Stack Arrays Collections Comparable Comparator Iterator Optional StringBuilder ' +
  'Exception RuntimeException Thread Stream Collectors Number Void IO Objects Random Scanner Entry').split(' '));

const esc = (s) => s.replace(/[&<>"]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));

function highlightLine(line, state) {
  let out = '';
  let i = 0;
  const n = line.length;

  const push = (cls, text) => { out += cls ? `<span class="tok-${cls}">${esc(text)}</span>` : esc(text); };

  while (i < n) {
    // continuing a block comment
    if (state.block) {
      const end = line.indexOf('*/', i);
      if (end === -1) { push('com', line.slice(i)); i = n; break; }
      push('com', line.slice(i, end + 2));
      i = end + 2;
      state.block = false;
      continue;
    }
    // continuing a text block
    if (state.text) {
      const end = line.indexOf('"""', i);
      if (end === -1) { push('str', line.slice(i)); i = n; break; }
      push('str', line.slice(i, end + 3));
      i = end + 3;
      state.text = false;
      continue;
    }

    const c = line[i];
    const two = line.slice(i, i + 2);

    if (two === '//') { push('com', line.slice(i)); break; }

    if (two === '/*') {
      const end = line.indexOf('*/', i + 2);
      if (end === -1) { push('com', line.slice(i)); state.block = true; break; }
      push('com', line.slice(i, end + 2));
      i = end + 2;
      continue;
    }

    if (line.slice(i, i + 3) === '"""') {
      const end = line.indexOf('"""', i + 3);
      if (end === -1) { push('str', line.slice(i)); state.text = true; break; }
      push('str', line.slice(i, end + 3));
      i = end + 3;
      continue;
    }

    if (c === '"' || c === "'") {
      let j = i + 1;
      while (j < n) {
        if (line[j] === '\\') { j += 2; continue; }
        if (line[j] === c) { j++; break; }
        j++;
      }
      push('str', line.slice(i, j));
      i = j;
      continue;
    }

    if (/[0-9]/.test(c) && !/[A-Za-z_$]/.test(line[i - 1] || ' ')) {
      const m = /^(0[xXbB][0-9a-fA-F_]+|[0-9][0-9_]*\.?[0-9_]*(?:[eE][-+]?[0-9]+)?)[lLfFdD]?/.exec(line.slice(i));
      if (m) { push('num', m[0]); i += m[0].length; continue; }
    }

    if (/[A-Za-z_$]/.test(c)) {
      const m = /^[A-Za-z_$][\w$]*/.exec(line.slice(i));
      const word = m[0];
      i += word.length;
      // what follows decides between a call and a plain identifier
      let k = i;
      while (k < n && line[k] === ' ') k++;
      if (JAVA_KEYWORDS.has(word)) push('key', word);
      else if (line[k] === '(') push('fn', word);
      else if (JAVA_SOFT.has(word) || /^[A-Z]/.test(word)) push('type', word);
      else push(null, word);
      continue;
    }

    if (/[{}()[\];,.<>=+\-*/%!&|^~?:]/.test(c)) {
      const m = /^[{}()[\];,.<>=+\-*/%!&|^~?:]+/.exec(line.slice(i));
      push('punc', m[0]);
      i += m[0].length;
      continue;
    }

    push(null, c);
    i++;
  }
  return out;
}

/** @returns {string} HTML for a <code> block, one line per source line. */
function highlightJava(source, { from = 1, numbers = true } = {}) {
  const state = { block: false, text: false };
  const lines = source.split('\n');
  let html = '';
  for (let idx = 0; idx < lines.length; idx++) {
    const ln = numbers ? `<span class="ln">${from + idx}</span>` : '';
    html += ln + highlightLine(lines[idx], state) + '\n';
  }
  return html;
}

/* ================================================================ store ==
 * Progress lives in localStorage, which throws outright in some contexts
 * (private windows, blocked site data, thumbnail capture). Every access is
 * guarded and the app must render correctly with nothing stored.
 */

const LS_KEY = 'dsa.deck.v1';

const store = {
  data: { progress: {}, prefs: {} },
  ok: true,

  load() {
    try {
      const raw = localStorage.getItem(LS_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        this.data.progress = parsed.progress ?? {};
        this.data.prefs = parsed.prefs ?? {};
      }
    } catch { this.ok = false; }
  },

  save() {
    if (!this.ok) return;
    try { localStorage.setItem(LS_KEY, JSON.stringify(this.data)); } catch { this.ok = false; }
  },

  get(id) { return this.data.progress[id] ?? null; },
  pref(k, v) {
    if (v === undefined) return this.data.prefs[k];
    this.data.prefs[k] = v;
    this.save();
    return v;
  },
};

/* ---- spaced repetition ---------------------------------------------------
 * Deliberately simple and legible: a five-rung interval ladder. "Solid"
 * climbs a rung, "Shaky" holds and comes back in 3 days, "Forgot" resets to
 * tomorrow. Predictable beats clever when you can see your own schedule.
 */

const LADDER = [1, 3, 7, 16, 35];
const GRADES = { 1: 'forgot', 2: 'shaky', 3: 'solid' };

const todayISO = () => new Date().toISOString().slice(0, 10);
const addDays = (iso, d) => {
  const t = new Date(iso + 'T00:00:00');
  t.setDate(t.getDate() + d);
  return t.toISOString().slice(0, 10);
};
const daysBetween = (a, b) => Math.round((new Date(b + 'T00:00:00') - new Date(a + 'T00:00:00')) / 864e5);

function grade(id, g) {
  const prev = store.get(id);
  let step = prev?.step ?? 0;
  let interval;

  if (g === 3) { step = Math.min(step + 1, LADDER.length - 1); interval = LADDER[step]; }
  else if (g === 2) { interval = 3; }
  else { step = 0; interval = 1; }

  store.data.progress[id] = {
    grade: g,
    step,
    reps: (prev?.reps ?? 0) + 1,
    last: todayISO(),
    due: addDays(todayISO(), interval),
  };
  store.save();
  return interval;
}

const statusOf = (id) => GRADES[store.get(id)?.grade] ?? null;
const isDue = (id) => {
  const r = store.get(id);
  return !!r && r.due <= todayISO();
};

/* ================================================================= state == */

const app = {
  data: null,
  byId: new Map(),
  index: [],
  route: { name: 'today', arg: null },
  query: '',
  listing: [],   // current visible problem list, drives j/k and cards
  cursor: -1,
  session: null, // flashcard session
};

/* ================================================================= utils == */

const el = (sel, root = document) => root.querySelector(sel);
const h = (html) => { const t = document.createElement('template'); t.innerHTML = html.trim(); return t.content.firstElementChild; };

function toast(msg) {
  const t = el('#toast');
  t.textContent = msg;
  t.classList.add('on');
  clearTimeout(toast._t);
  toast._t = setTimeout(() => t.classList.remove('on'), 1900);
}

const plural = (n, s, p) => `${n} ${n === 1 ? s : (p ?? s + 's')}`;

function relDue(due) {
  const d = daysBetween(todayISO(), due);
  if (d < 0) return `${plural(-d, 'day')} overdue`;
  if (d === 0) return 'due today';
  if (d === 1) return 'due tomorrow';
  return `due in ${plural(d, 'day')}`;
}

const catName = (catId) => app.data.categories.find((c) => c.id === catId)?.name ?? catId;

/* ================================================================ search == */

function buildIndex() {
  app.index = app.data.problems.map((p) => ({
    id: p.id,
    title: p.title.toLowerCase(),
    ids: p.ids.map(String),
    cat: (catName(p.category) + ' ' + p.categoryPath).toLowerCase(),
    cls: p.className.toLowerCase(),
    body: (p.notes.join(' ') + ' ' + (p.statement ?? '') + ' ' + (p.teaches ?? '')).toLowerCase(),
    code: p.source.toLowerCase(),
  }));
}

function search(q) {
  const needle = q.trim().toLowerCase();
  if (!needle) return null;
  const terms = needle.split(/\s+/);
  const scored = [];

  for (const e of app.index) {
    let score = 0;
    for (const t of terms) {
      const bare = t.replace(/^#/, '');
      let s = 0;
      if (/^\d+$/.test(bare) && e.ids.includes(bare)) s = 100;
      else if (e.title === t) s = 90;
      else if (e.title.startsWith(t)) s = 60;
      else if (e.title.includes(t)) s = 40;
      else if (e.cls.includes(t)) s = 30;
      else if (e.cat.includes(t)) s = 18;
      else if (e.body.includes(t)) s = 10;
      else if (e.code.includes(t)) s = 4;
      if (s === 0) { score = 0; break; }
      score += s;
    }
    if (score > 0) scored.push({ id: e.id, score });
  }

  scored.sort((a, b) => b.score - a.score || a.id.localeCompare(b.id));
  return scored.map((s) => app.byId.get(s.id));
}

/* ============================================================== fragments == */

function diffChip(d) {
  return d ? `<span class="diff ${d.toLowerCase()}">${d}</span>` : '';
}

function problemRow(p, { selected = false } = {}) {
  const st = statusOf(p.id);
  const rec = store.get(p.id);
  const num = p.ids.length ? `#${p.ids[0]}` : '';
  const due = rec && isDue(p.id) ? '<span class="chip due">due</span>' : '';
  const qual = p.titleQualifier ? ` <span class="qualifier">· ${esc(p.titleQualifier)}</span>` : '';

  return `<a class="pcard${selected ? ' sel' : ''}" href="#/p/${p.id}" data-st="${st ?? ''}" data-id="${p.id}">
    <span class="pcard-stripe"></span>
    <span class="pcard-id">${num}</span>
    <span class="pcard-main">
      <span class="pcard-title">${esc(p.title)}${qual}</span>
      <span class="pcard-meta">
        <span>${esc(catName(p.category))}</span>
        ${p.notes.length || p.authorNotes.length ? '<span>· notes</span>' : ''}
        ${rec ? `<span>· ${esc(relDue(rec.due))}</span>` : ''}
      </span>
    </span>
    <span class="pcard-right">
      ${due}
      ${diffChip(p.difficulty)}
      <span class="stdot ${st ?? ''}" title="${st ?? 'not reviewed'}"></span>
    </span>
  </a>`;
}

function problemList(items) {
  if (!items.length) return '';
  return `<div class="plist">${items.map((p) => problemRow(p)).join('')}</div>`;
}

function extLinks(p) {
  const out = [];
  if (p.canonicalUrl) {
    const isSearch = p.urlSource === 'search-fallback';
    const label = isSearch ? `Search #${p.ids[0]}` : (p.site === 'geeksforgeeks.org' ? 'GeeksforGeeks' : p.site === 'hackerrank.com' ? 'HackerRank' : 'LeetCode');
    out.push(`<a class="extlink${isSearch ? ' is-search' : ''}" href="${p.canonicalUrl}" target="_blank" rel="noopener">
      <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M11 4h5v5M16 4l-7 7M8 5H4v11h11v-4"/></svg>${label}</a>`);
  }
  for (const u of p.urls) {
    if (u.role === 'submission') {
      out.push(`<a class="extlink" href="${u.raw}" target="_blank" rel="noopener">
        <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M4 10.5l4 4 8-9"/></svg>My submission</a>`);
      break;
    }
  }
  return out.join('');
}

function codeBlock(p, { blurred = false, bodyOnly = false } = {}) {
  const lines = p.source.split('\n');
  const from = bodyOnly ? p.bodyStartLine : 1;
  const src = bodyOnly ? lines.slice(p.bodyStartLine - 1, p.bodyEndLine).join('\n') : p.source;

  const jumps = (p.signatureLines ?? []).length > 1
    ? `<div class="jumps">${p.signatureLines.map((s) =>
        `<button class="jump" type="button" data-act="jump" data-line="${s.line}">${esc(s.name)}${s.kind === 'main' ? '()' : ''}</button>`).join('')}</div>`
    : '';

  return `<div class="codewrap${blurred ? ' blurred' : ''}" id="codewrap">
    <div class="code-bar">
      <span class="code-file">${esc(p.path)}</span>
      <button class="code-btn" type="button" data-act="bodyonly" aria-pressed="${bodyOnly}">Hide header</button>
      <button class="code-btn" type="button" data-act="copy">Copy</button>
    </div>
    ${jumps}
    <pre class="code"><code>${highlightJava(src, { from })}</code></pre>
    ${blurred ? `<div class="reveal">
      <button class="reveal-btn" type="button" data-act="reveal">Reveal solution</button>
      <span class="reveal-hint">recall the approach first</span>
    </div>` : ''}
  </div>`;
}

function statementBlock(p) {
  if (!p.statement) return '';
  const gen = p.statementSource === 'generated';
  return `<section class="block">
    <h2 class="block-h">${gen ? 'What this problem asks' : 'Problem'}${gen ? ' <span class="chip gen">generated</span>' : ''}</h2>
    <div class="prose">${esc(p.statement)}</div>
  </section>`;
}

function examplesBlock(p) {
  if (!p.examples.length) return '';
  const rows = p.examples.map((e, i) => `<div class="ex">
    ${p.examples.length > 1 ? `<div class="ex-n">Example ${e.index ?? i + 1}</div>` : ''}
    <div class="ex-rows">
      ${e.input ? `<div class="ex-k">In</div><div class="ex-v">${esc(e.input)}</div>` : ''}
      ${e.output ? `<div class="ex-k">Out</div><div class="ex-v">${esc(e.output)}</div>` : ''}
      ${e.explanation ? `<div class="ex-k">Why</div><div class="ex-v expl">${esc(e.explanation)}</div>` : ''}
    </div>
  </div>`).join('');
  return `<section class="block"><h2 class="block-h">Sample input &amp; output</h2><div class="exlist">${rows}</div></section>`;
}

function notesBlock(p) {
  const parts = [];
  if (p.notes.length) {
    parts.push(`<div class="mynotes">${p.notes.map((n) => `<div class="noteline">${esc(n)}</div>`).join('')}</div>`);
  }
  for (const n of p.authorNotes) {
    parts.push(`<div class="mynotes"><pre>${esc(n.text)}</pre></div>`);
  }
  if (!parts.length) return '';
  return `<section class="block"><h2 class="block-h">My notes</h2>${parts.join('')}</section>`;
}

function teachesBlock(p) {
  if (!p.teaches) return '';
  return `<section class="block">
    <h2 class="block-h">Why it matters <span class="chip gen">generated</span></h2>
    <div class="teaches">${esc(p.teaches)}${p.complexity ? `<br><br><b>${esc(p.complexity)}</b>` : ''}</div>
  </section>`;
}

function constraintsBlock(p) {
  if (!p.constraints.length) return '';
  return `<section class="block"><h2 class="block-h">Constraints</h2>
    <ul class="cons">${p.constraints.map((c) => `<li>${esc(c)}</li>`).join('')}</ul></section>`;
}

function altBlock(p) {
  if (!p.alternateSolutions.length) return '';
  return p.alternateSolutions.map((a) => `<section class="block">
    <h2 class="block-h">${esc(a.label)}</h2>
    <div class="codewrap"><pre class="code"><code>${highlightJava(a.code, { numbers: false })}</code></pre></div>
  </section>`).join('');
}

function grader(p) {
  const rec = store.get(p.id);
  const g = rec?.grade;
  return `<div class="grader">
    <span class="grader-lab">How well do you know it</span>
    <button class="gbtn g1" type="button" data-act="grade" data-g="1" aria-pressed="${g === 1}">Forgot</button>
    <button class="gbtn g2" type="button" data-act="grade" data-g="2" aria-pressed="${g === 2}">Shaky</button>
    <button class="gbtn g3" type="button" data-act="grade" data-g="3" aria-pressed="${g === 3}">Solid</button>
    <span class="grader-when">${rec ? `reviewed ${plural(rec.reps, 'time')} · ${esc(relDue(rec.due))}` : 'not reviewed yet'}</span>
  </div>`;
}

/* ================================================================= views == */

function viewToday() {
  const all = app.data.problems.filter((p) => p.kind !== 'scratch');
  const due = all.filter((p) => isDue(p.id));
  const seen = all.filter((p) => store.get(p.id));
  const solid = all.filter((p) => statusOf(p.id) === 'solid').length;
  const weak = all.filter((p) => ['shaky', 'forgot'].includes(statusOf(p.id))).length;

  // Never show an empty shell: with nothing scheduled, offer the real backlog.
  // Sampled one-per-category so the first screen shows the breadth of the
  // repo rather than twelve consecutive files from whichever topic sorts first.
  const unseen = all.filter((p) => !store.get(p.id));
  const seenCats = new Set();
  const fresh = unseen.filter((p) => {
    if (seenCats.has(p.category)) return false;
    seenCats.add(p.category);
    return true;
  }).slice(0, 12);
  const queue = due.length ? due : fresh;

  const meta = app.data.meta;

  return `<div class="wrap">
    <div class="pagehead">
      <span class="eyebrow">${esc(meta.git.shortCommit ?? 'local')} · ${meta.counts.files} files</span>
    </div>
    <div class="pagehead">
      <h1>Today</h1>
      <p>${due.length ? `${plural(due.length, 'problem')} scheduled for review.` : 'Nothing scheduled — start anywhere below.'}</p>
    </div>

    <div class="statstrip" style="margin-top:14px">
      <div class="stat is-due"><span class="stat-k">Due</span><span class="stat-v">${due.length}</span></div>
      <div class="stat"><span class="stat-k">Solid</span><span class="stat-v">${solid}</span></div>
      <div class="stat"><span class="stat-k">Needs work</span><span class="stat-v">${weak}</span></div>
      <div class="stat"><span class="stat-k">Untouched</span><span class="stat-v">${all.length - seen.length}<small>/${all.length}</small></span></div>
    </div>

    ${due.length ? `<div class="section">
      <div class="section-head">
        <h2>Due for review</h2>
        <span class="count">${due.length}</span>
      </div>
      ${problemList(due.slice(0, 25))}
      <div class="setup-row" style="margin-top:12px">
        <a class="bigbtn" href="#/cards/due">Review ${Math.min(due.length, 20)} as flashcards</a>
      </div>
    </div>` : `<div class="section">
      <div class="section-head"><h2>Start here</h2><span class="count">not yet reviewed</span></div>
      ${problemList(queue)}
      <div class="setup-row" style="margin-top:12px">
        <a class="bigbtn" href="#/cards">Start a flashcard session</a>
        <a class="bigbtn ghost" href="#/browse">Browse all ${all.length}</a>
      </div>
    </div>`}

    <div class="section">
      <div class="section-head"><h2>By category</h2><span class="count">${app.data.categories.filter((c) => c.counts.files).length} groups</span></div>
      <div class="bars">${categoryBars()}</div>
    </div>
  </div>`;
}

function categoryBars() {
  return app.data.categories
    .filter((c) => c.counts.files > 0 && c.id !== 'root')
    .map((c) => {
      const items = app.data.problems.filter((p) => p.category === c.id);
      const n = items.length;
      const cnt = { solid: 0, shaky: 0, forgot: 0 };
      for (const p of items) {
        const s = statusOf(p.id);
        if (s) cnt[s]++;
      }
      const none = n - cnt.solid - cnt.shaky - cnt.forgot;
      const pct = (x) => (x / n) * 100;
      return `<a class="barrow" href="#/browse/${c.id}">
        <span class="barrow-k">${esc(c.name)}</span>
        <span class="barrow-t">
          <i class="solid" style="width:${pct(cnt.solid)}%"></i>
          <i class="shaky" style="width:${pct(cnt.shaky)}%"></i>
          <i class="forgot" style="width:${pct(cnt.forgot)}%"></i>
          <i class="none" style="width:${pct(none)}%"></i>
        </span>
        <span class="barrow-n">${cnt.solid}/${n}</span>
      </a>`;
    }).join('');
}

function viewBrowse(catId) {
  const results = search(app.query);
  let items;
  let title;
  let sub;

  if (results) {
    items = results;
    title = 'Search';
    sub = `${plural(items.length, 'match', 'matches')} for “${esc(app.query.trim())}”`;
  } else if (catId) {
    items = app.data.problems.filter((p) => p.category === catId);
    title = catName(catId);
    const c = app.data.categories.find((x) => x.id === catId);
    sub = `${plural(items.length, 'file')}${c ? ` · ${c.counts.problems} with a problem link` : ''}`;
  } else {
    items = app.data.problems.filter((p) => p.kind !== 'scratch');
    title = 'All problems';
    sub = `${plural(items.length, 'file')} across ${app.data.categories.filter((x) => x.counts.files).length} categories`;
  }

  app.listing = items;

  if (!items.length) {
    return `<div class="wrap"><div class="pagehead"><h1>${esc(title)}</h1></div>
      <div class="empty" style="margin-top:16px">
        <h3>No matches</h3>
        <p>Nothing matched “${esc(app.query)}”. Search covers titles, LeetCode ids, your notes and the code itself.</p>
        <button class="bigbtn ghost" type="button" data-act="clear-search">Clear search</button>
      </div></div>`;
  }

  return `<div class="wrap">
    <div class="pagehead"><h1>${esc(title)}</h1><p>${sub}</p></div>
    <div class="section" style="margin-top:16px">${problemList(items)}</div>
  </div>`;
}

function viewProblem(id) {
  const p = app.byId.get(id);
  if (!p) return `<div class="wrap"><div class="empty"><h3>Not found</h3><p>No problem with id <code>${esc(id)}</code>.</p><a class="bigbtn ghost" href="#/browse">Browse all</a></div></div>`;

  const idx = app.listing.findIndex((x) => x.id === id);
  const prev = idx > 0 ? app.listing[idx - 1] : null;
  const next = idx >= 0 && idx < app.listing.length - 1 ? app.listing[idx + 1] : null;

  const num = p.ids.length ? `<span class="num">${p.ids.map((i) => '#' + i).join(' ')}</span> ` : '';

  return `<article class="pdetail" data-pid="${p.id}">
    <div class="pd-top">
      <a class="backlink" href="#/browse/${p.category}">
        <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M11 5l-5 5 5 5"/></svg>${esc(catName(p.category))}
      </a>
      ${p.categoryPath && p.categoryPath !== catName(p.category)
        ? `<span class="pd-crumb">${esc(p.categoryPath)}</span>` : ''}
    </div>

    <h1 class="pd-title">${num}${esc(p.title)}</h1>

    <div class="pd-meta">
      ${diffChip(p.difficulty)}
      ${p.companies.map((c) => `<span class="chip">${esc(c)}</span>`).join('')}
      ${p.status ? `<span class="chip">${p.status === 'tle' ? 'time limit exceeded' : 'incomplete'}</span>` : ''}
      ${p.solvedDate ? `<span class="chip">solved ${esc(p.solvedDate)}</span>` : ''}
      ${p.kind === 'concept' ? '<span class="chip">concept</span>' : ''}
      ${extLinks(p)}
    </div>

    ${grader(p)}
    ${statementBlock(p)}
    ${examplesBlock(p)}
    ${constraintsBlock(p)}
    ${notesBlock(p)}
    ${teachesBlock(p)}

    <section class="block">
      <h2 class="block-h">Solution</h2>
      ${codeBlock(p, { bodyOnly: !!store.pref('bodyOnly') })}
    </section>

    ${altBlock(p)}

    <div class="setup-row" style="margin:24px 0 8px">
      ${prev ? `<a class="bigbtn ghost" href="#/p/${prev.id}">← ${esc(prev.title.slice(0, 26))}</a>` : ''}
      ${next ? `<a class="bigbtn ghost" href="#/p/${next.id}">${esc(next.title.slice(0, 26))} →</a>` : ''}
    </div>
  </article>`;
}

/* ---- flashcards ---------------------------------------------------------- */

function startSession(scope) {
  const all = app.data.problems.filter((p) => p.kind !== 'scratch');
  let pool;

  if (scope === 'due') pool = all.filter((p) => isDue(p.id));
  else if (scope && scope !== 'all') pool = all.filter((p) => p.category === scope);
  else pool = all;

  // Prefer material that is due or never seen, then fill with the rest.
  const priority = pool.filter((p) => isDue(p.id) || !store.get(p.id));
  const rest = pool.filter((p) => !priority.includes(p));
  const deck = shuffle(priority).concat(shuffle(rest)).slice(0, 20);

  app.session = { deck, i: 0, revealed: false, scope: scope ?? 'all', done: 0 };
  return deck.length;
}

function shuffle(a) {
  const out = a.slice();
  for (let i = out.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [out[i], out[j]] = [out[j], out[i]];
  }
  return out;
}

function viewCards(scope) {
  if (!app.session || (scope && app.session.scope !== scope)) {
    const n = startSession(scope);
    if (!n) {
      return `<div class="cardstage"><div class="empty">
        <h3>Nothing to review here</h3>
        <p>That selection has no problems in it. Pick a different category, or review everything.</p>
        <a class="bigbtn" href="#/cards/all">Review everything</a>
      </div></div>`;
    }
  }

  const s = app.session;

  if (s.i >= s.deck.length) {
    return `<div class="cardstage"><div class="empty">
      <h3>Session complete</h3>
      <p>You graded ${plural(s.done, 'problem')}. Your next review dates are set — check Today to see what comes back when.</p>
      <div class="setup-row" style="justify-content:center">
        <a class="bigbtn" href="#/today">See schedule</a>
        <button class="bigbtn ghost" type="button" data-act="new-session">Another session</button>
      </div>
    </div></div>`;
  }

  const p = s.deck[s.i];
  const pct = (s.i / s.deck.length) * 100;

  return `<div class="cardstage" data-pid="${p.id}">
    <div class="card-progress">
      <span>Card ${s.i + 1} / ${s.deck.length}</span>
      <span class="bar"><i style="width:${pct}%"></i></span>
      <span>${esc(s.scope === 'due' ? 'due queue' : s.scope === 'all' ? 'all topics' : catName(s.scope))}</span>
    </div>

    <div class="cardface">
      <div class="pd-meta" style="margin-bottom:10px">
        <span class="chip">${esc(catName(p.category))}</span>
        ${diffChip(p.difficulty)}
        ${p.ids.length ? `<span class="chip">#${p.ids[0]}</span>` : ''}
      </div>

      <h1 class="pd-title">${esc(p.title)}</h1>

      ${p.statement ? `<div class="prose" style="margin-bottom:14px">${esc(truncate(p.statement, 420))}</div>`
        : `<div class="prose dim" style="margin-bottom:14px">No statement recorded — recall what <code>${esc(p.className)}</code> does from the title and category.</div>`}

      ${p.examples.length ? `<div class="ex" style="margin-bottom:14px">
        <div class="ex-rows">
          ${p.examples[0].input ? `<div class="ex-k">In</div><div class="ex-v">${esc(p.examples[0].input)}</div>` : ''}
          ${p.examples[0].output ? `<div class="ex-k">Out</div><div class="ex-v">${esc(p.examples[0].output)}</div>` : ''}
        </div>
      </div>` : ''}

      ${codeBlock(p, { blurred: !s.revealed, bodyOnly: true })}

      ${s.revealed ? `<div class="cardgrade">
        <button class="gbtn g1" type="button" data-act="card-grade" data-g="1"><b>Forgot</b><span>back tomorrow</span></button>
        <button class="gbtn g2" type="button" data-act="card-grade" data-g="2"><b>Shaky</b><span>back in 3 days</span></button>
        <button class="gbtn g3" type="button" data-act="card-grade" data-g="3"><b>Solid</b><span>push it out</span></button>
      </div>` : ''}

      <div class="setup-row" style="margin-top:14px">
        <a class="bigbtn ghost" href="#/p/${p.id}">Open full problem</a>
        <button class="bigbtn ghost" type="button" data-act="card-skip">Skip</button>
      </div>
    </div>
  </div>`;
}

const truncate = (s, n) => (s.length <= n ? s : s.slice(0, n).replace(/\s+\S*$/, '') + '…');

/* ---- progress ------------------------------------------------------------ */

function viewStats() {
  const all = app.data.problems.filter((p) => p.kind !== 'scratch');
  const cnt = { solid: 0, shaky: 0, forgot: 0 };
  for (const p of all) { const s = statusOf(p.id); if (s) cnt[s]++; }
  const none = all.length - cnt.solid - cnt.shaky - cnt.forgot;
  const reps = Object.values(store.data.progress).reduce((n, r) => n + (r.reps ?? 0), 0);

  const upcoming = {};
  for (const p of all) {
    const r = store.get(p.id);
    if (!r) continue;
    const d = Math.max(0, daysBetween(todayISO(), r.due));
    const bucket = d === 0 ? 'today' : d === 1 ? 'tomorrow' : d <= 7 ? 'this week' : d <= 30 ? 'this month' : 'later';
    upcoming[bucket] = (upcoming[bucket] ?? 0) + 1;
  }

  const meta = app.data.meta;

  return `<div class="wrap narrow">
    <div class="pagehead"><h1>Progress</h1><p>${plural(reps, 'review')} logged across ${plural(all.length, 'problem')}.</p></div>

    <div class="statstrip" style="margin-top:14px">
      <div class="stat"><span class="stat-k">Solid</span><span class="stat-v" style="color:var(--good)">${cnt.solid}</span></div>
      <div class="stat"><span class="stat-k">Shaky</span><span class="stat-v" style="color:var(--warn)">${cnt.shaky}</span></div>
      <div class="stat"><span class="stat-k">Forgot</span><span class="stat-v" style="color:var(--crit)">${cnt.forgot}</span></div>
      <div class="stat"><span class="stat-k">Untouched</span><span class="stat-v">${none}</span></div>
    </div>

    ${reps ? `<div class="section">
      <div class="section-head"><h2>Coming back</h2></div>
      <table class="kvtable">
        <tbody>${['today', 'tomorrow', 'this week', 'this month', 'later']
          .filter((k) => upcoming[k]).map((k) => `<tr><th>${k}</th><td class="n">${upcoming[k]}</td></tr>`).join('')}</tbody>
      </table>
    </div>` : ''}

    <div class="section">
      <div class="section-head"><h2>By category</h2></div>
      <div class="legend">
        <span><i class="stdot solid"></i> solid</span>
        <span><i class="stdot shaky"></i> shaky</span>
        <span><i class="stdot forgot"></i> forgot</span>
        <span><i class="stdot"></i> not reviewed</span>
      </div>
      <div class="bars">${categoryBars()}</div>
    </div>

    <div class="section">
      <div class="section-head"><h2>Your progress data</h2></div>
      <p style="color:var(--text-dim);font-size:13px;margin:0 0 12px">
        ${store.ok
          ? 'Grades are stored in this browser only — they never leave your device, so they do not follow you to another phone or survive clearing site data. Export a copy to keep them.'
          : 'This browser is blocking local storage, so grades cannot be saved this session. Everything else works.'}
      </p>
      <div class="setup-row">
        <button class="bigbtn ghost" type="button" data-act="export">Export progress</button>
        <button class="bigbtn ghost" type="button" data-act="import">Import progress</button>
        <button class="bigbtn ghost" type="button" data-act="reset">Reset all</button>
      </div>
    </div>

    <div class="section">
      <div class="section-head"><h2>This build</h2></div>
      <table class="kvtable"><tbody>
        <tr><th>Source files</th><td class="n">${meta.counts.files}</td></tr>
        <tr><th>With a problem link</th><td class="n">${meta.counts.withCanonicalUrl}</td></tr>
        <tr><th>With a statement</th><td class="n">${meta.counts.withStatement}</td></tr>
        <tr><th>With your notes</th><td class="n">${meta.counts.withNotes}</td></tr>
        <tr><th>Commit</th><td class="n">${esc(meta.git.shortCommit ?? '—')}</td></tr>
        <tr><th>Generated</th><td class="n">${esc((meta.generatedAt ?? '').slice(0, 10) || '—')}</td></tr>
      </tbody></table>
    </div>
  </div>`;
}

/* ================================================================= render == */

function renderRail() {
  // 'root' holds only the IDE scratch files; they stay reachable by search
  // and direct link, but they are not a topic worth a rail row.
  const cats = app.data.categories.filter((c) => c.counts.files > 0 && c.id !== 'root');
  const cur = app.route.name === 'browse' ? app.route.arg
    : app.route.name === 'problem' ? app.byId.get(app.route.arg)?.category : null;

  const rows = cats.map((c) => {
    const items = app.data.problems.filter((p) => p.category === c.id);
    const solid = items.filter((p) => statusOf(p.id) === 'solid').length;
    const pct = items.length ? (solid / items.length) * 100 : 0;
    return `<a class="catrow${c.depth > 1 ? ' sub' : ''}" href="#/browse/${c.id}" aria-current="${cur === c.id}">
      <span class="catrow-name">${esc(c.name)}</span>
      <span class="catrow-n">${c.counts.files}</span>
      ${solid ? `<span class="catrow-meter"><i style="width:${pct}%"></i></span>` : ''}
    </a>`;
  }).join('');

  el('#rail-list').innerHTML = `<div class="rail-head">Topics</div>
    <a class="catrow" href="#/browse" aria-current="${app.route.name === 'browse' && !app.route.arg}">
      <span class="catrow-name">All problems</span>
      <span class="catrow-n">${app.data.problems.filter((p) => p.kind !== 'scratch').length}</span>
    </a>
    ${rows}`;
}

function render() {
  const main = el('#main');
  const r = app.route;

  if (r.name === 'today') main.innerHTML = viewToday();
  else if (r.name === 'browse') main.innerHTML = viewBrowse(r.arg);
  else if (r.name === 'problem') main.innerHTML = viewProblem(r.arg);
  else if (r.name === 'cards') main.innerHTML = viewCards(r.arg);
  else if (r.name === 'stats') main.innerHTML = viewStats();

  renderRail();

  // tab + badge state
  const due = app.data.problems.filter((p) => p.kind !== 'scratch' && isDue(p.id)).length;
  el('#tab-due').dataset.n = String(due);
  document.querySelectorAll('.tabbar a').forEach((a) => {
    a.setAttribute('aria-current', String(a.dataset.tab === r.name || (r.name === 'problem' && a.dataset.tab === 'browse')));
  });
  el('#brand-sub').textContent = due ? `${due} due` : 'revision deck';

  app.cursor = -1;
  if (r.name !== 'problem') window.scrollTo(0, 0);
}

/* ================================================================= router == */

function parseHash() {
  const raw = location.hash.replace(/^#\/?/, '');
  if (!raw) return { name: 'today', arg: null };
  const [head, ...rest] = raw.split('/');
  const tail = rest.join('/');
  if (head === 'p') return { name: 'problem', arg: tail };
  if (head === 'browse') return { name: 'browse', arg: tail || null };
  if (head === 'cards') return { name: 'cards', arg: tail || null };
  if (head === 'stats') return { name: 'stats', arg: null };
  return { name: 'today', arg: null };
}

function onRoute() {
  app.route = parseHash();
  if (app.route.name !== 'browse' && app.query) { app.query = ''; el('#q').value = ''; }
  el('#rail').dataset.open = 'false';
  el('.railtoggle').setAttribute('aria-expanded', 'false');
  render();
}

/* =================================================================== wire == */

function moveCursor(delta) {
  const cards = [...document.querySelectorAll('.pcard')];
  if (!cards.length) return;
  app.cursor = Math.max(0, Math.min(cards.length - 1, app.cursor + delta));
  cards.forEach((c, i) => c.classList.toggle('sel', i === app.cursor));
  cards[app.cursor].scrollIntoView({ block: 'nearest' });
}

function doGrade(id, g) {
  const days = grade(id, g);
  toast(`${GRADES[g][0].toUpperCase() + GRADES[g].slice(1)} — back in ${plural(days, 'day')}`);
}

function exportProgress() {
  const payload = JSON.stringify({ kind: 'dsa-deck-progress', version: 1, exported: new Date().toISOString(), progress: store.data.progress }, null, 2);
  // The artifact sandbox blocks downloads a page starts itself, so show the
  // data and copy it instead of handing over a file that may never arrive.
  navigator.clipboard?.writeText(payload).then(
    () => openSheet('Progress exported', `<p style="color:var(--text-dim);font-size:13px;margin:0 0 10px">Copied to your clipboard. Paste it somewhere safe — a note, a gist, a file. Import it back on another device.</p>
      <pre class="code" style="max-height:38dvh;border:1px solid var(--line);border-radius:var(--r)">${esc(payload)}</pre>`),
    () => openSheet('Progress export', `<p style="color:var(--text-dim);font-size:13px;margin:0 0 10px">Select all and copy this, then keep it somewhere safe.</p>
      <pre class="code" style="max-height:38dvh;border:1px solid var(--line);border-radius:var(--r)">${esc(payload)}</pre>`),
  );
}

function importProgress() {
  openSheet('Import progress', `<p style="color:var(--text-dim);font-size:13px;margin:0 0 10px">Paste an exported progress file. This replaces what is currently stored.</p>
    <textarea id="imp" rows="8" style="width:100%;background:var(--bg-sunk);color:var(--text);border:1px solid var(--line);border-radius:var(--r);padding:10px;font:400 11.5px var(--mono)" spellcheck="false"></textarea>
    <div class="setup-row" style="margin-top:10px"><button class="bigbtn" type="button" data-act="import-go">Import</button></div>`);
}

function openSheet(title, bodyHtml) {
  el('#sheet-title').textContent = title;
  el('#sheet-body').innerHTML = bodyHtml;
  el('#sheet').hidden = false;
}

function closeSheet() { el('#sheet').hidden = true; }

const SHORTCUTS = [
  ['/', 'Focus search'],
  ['j / k', 'Move down / up the list'],
  ['Enter', 'Open the highlighted problem'],
  ['r', 'Jump to a random problem'],
  ['f', 'Start a flashcard session'],
  ['Space', 'Reveal the solution on a card'],
  ['1 / 2 / 3', 'Grade Forgot / Shaky / Solid'],
  ['g then t', 'Go to Today'],
  ['g then b', 'Go to Browse'],
  ['Esc', 'Close this, or clear search'],
  ['?', 'Show this list'],
];

function helpSheet() {
  openSheet('Keyboard shortcuts', `<div class="kbdlist">${SHORTCUTS.map(([k, d]) => `<kbd>${esc(k)}</kbd><span>${esc(d)}</span>`).join('')}</div>`);
}

function cycleTheme() {
  const cur = document.documentElement.dataset.theme ?? 'system';
  const next = cur === 'system' ? 'dark' : cur === 'dark' ? 'light' : 'system';
  if (next === 'system') delete document.documentElement.dataset.theme;
  else document.documentElement.dataset.theme = next;
  store.pref('theme', next);
  toast(`Theme: ${next}`);
}

function randomProblem() {
  const all = app.data.problems.filter((p) => p.kind !== 'scratch');
  location.hash = `#/p/${all[Math.floor(Math.random() * all.length)].id}`;
}

document.addEventListener('click', (e) => {
  const t = e.target.closest('[data-act]');
  if (!t) return;
  const act = t.dataset.act;
  const stage = el('.pdetail') || el('.cardstage');
  const pid = stage?.dataset.pid;

  switch (act) {
    case 'toggle-rail': {
      const rail = el('#rail');
      const open = rail.dataset.open !== 'true';
      rail.dataset.open = String(open);
      t.setAttribute('aria-expanded', String(open));
      break;
    }
    case 'theme': cycleTheme(); break;
    case 'help': helpSheet(); break;
    case 'close-sheet': closeSheet(); break;
    case 'random': randomProblem(); break;
    case 'clear-search': app.query = ''; el('#q').value = ''; render(); break;

    case 'grade':
      if (pid) { doGrade(pid, Number(t.dataset.g)); render(); }
      break;

    case 'copy': {
      const p = app.byId.get(pid);
      if (p) navigator.clipboard?.writeText(p.source).then(() => toast('Code copied'), () => toast('Copy blocked by browser'));
      break;
    }

    case 'bodyonly': {
      store.pref('bodyOnly', !store.pref('bodyOnly'));
      render();
      break;
    }

    case 'jump': {
      const line = Number(t.dataset.line);
      const pre = el('#codewrap pre.code');
      const lns = [...pre.querySelectorAll('.ln')];
      const hit = lns.find((s) => Number(s.textContent) === line);
      if (hit) {
        hit.parentElement.querySelectorAll('mark').forEach((m) => m.replaceWith(...m.childNodes));
        hit.scrollIntoView({ block: 'center', behavior: 'smooth' });
        pre.scrollLeft = 0;
      }
      break;
    }

    case 'reveal':
      if (app.session) { app.session.revealed = true; render(); }
      else el('#codewrap')?.classList.remove('blurred');
      break;

    case 'card-grade':
      if (pid && app.session) {
        doGrade(pid, Number(t.dataset.g));
        app.session.done++;
        app.session.i++;
        app.session.revealed = false;
        render();
      }
      break;

    case 'card-skip':
      if (app.session) { app.session.i++; app.session.revealed = false; render(); }
      break;

    case 'new-session':
      app.session = null;
      startSession('all');
      render();
      break;

    case 'export': exportProgress(); break;
    case 'import': importProgress(); break;

    case 'import-go': {
      try {
        const parsed = JSON.parse(el('#imp').value);
        const incoming = parsed.progress ?? parsed;
        if (typeof incoming !== 'object' || Array.isArray(incoming)) throw new Error('not a progress object');
        store.data.progress = incoming;
        store.save();
        closeSheet();
        render();
        toast(`Imported ${plural(Object.keys(incoming).length, 'record')}`);
      } catch (err) {
        toast(`Could not read that: ${err.message}`);
      }
      break;
    }

    case 'reset':
      if (confirm('Clear every grade and review date? This cannot be undone.')) {
        store.data.progress = {};
        store.save();
        render();
        toast('Progress cleared');
      }
      break;
  }
});

let searchTimer;
document.addEventListener('input', (e) => {
  if (e.target.id !== 'q') return;
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    app.query = e.target.value;
    if (app.route.name !== 'browse') { location.hash = '#/browse'; return; }
    render();
  }, 110);
});

let gPending = false;
document.addEventListener('keydown', (e) => {
  const typing = /^(INPUT|TEXTAREA|SELECT)$/.test(e.target.tagName);

  if (e.key === 'Escape') {
    if (!el('#sheet').hidden) { closeSheet(); return; }
    if (typing) { e.target.blur(); return; }
    if (app.query) { app.query = ''; el('#q').value = ''; render(); }
    return;
  }

  if (typing) return;
  if (e.metaKey || e.ctrlKey || e.altKey) return;

  if (gPending) {
    gPending = false;
    if (e.key === 't') { location.hash = '#/today'; return; }
    if (e.key === 'b') { location.hash = '#/browse'; return; }
    if (e.key === 'c') { location.hash = '#/cards'; return; }
    if (e.key === 's') { location.hash = '#/stats'; return; }
  }

  switch (e.key) {
    case '/': e.preventDefault(); el('#q').focus(); el('#q').select(); break;
    case '?': helpSheet(); break;
    case 'g': gPending = true; break;
    case 'j': e.preventDefault(); moveCursor(1); break;
    case 'k': e.preventDefault(); moveCursor(-1); break;
    case 'r': randomProblem(); break;
    case 'f': location.hash = '#/cards'; break;
    case 'Enter': {
      const sel = el('.pcard.sel');
      if (sel) { e.preventDefault(); location.hash = sel.getAttribute('href'); }
      break;
    }
    case ' ': {
      if (app.session && !app.session.revealed && app.route.name === 'cards') {
        e.preventDefault();
        app.session.revealed = true;
        render();
      }
      break;
    }
    case '1': case '2': case '3': {
      const stage = el('.pdetail') || el('.cardstage');
      const pid = stage?.dataset.pid;
      if (!pid) break;
      if (app.route.name === 'cards') {
        if (!app.session?.revealed) break;
        doGrade(pid, Number(e.key));
        app.session.done++;
        app.session.i++;
        app.session.revealed = false;
      } else {
        doGrade(pid, Number(e.key));
      }
      render();
      break;
    }
  }
});

/* =================================================================== boot == */

async function boot() {
  store.load();

  const saved = store.pref('theme');
  if (saved && saved !== 'system') document.documentElement.dataset.theme = saved;

  el('#main').innerHTML = '<div class="loading">Loading deck…</div>';

  try {
    // Inlined by the single-file bundler; fetched on the hosted site.
    app.data = window.__DECK_DATA__ ?? await (await fetch('data.json', { cache: 'no-cache' })).json();
  } catch (err) {
    el('#main').innerHTML = `<div class="wrap"><div class="empty">
      <h3>Could not load the deck</h3>
      <p>data.json did not load (${esc(err.message)}). If you opened this file directly from disk, serve the folder over HTTP instead — browsers block local fetches.</p>
    </div></div>`;
    return;
  }

  for (const p of app.data.problems) app.byId.set(p.id, p);
  buildIndex();
  app.listing = app.data.problems.filter((p) => p.kind !== 'scratch');

  window.addEventListener('hashchange', onRoute);
  onRoute();

  // Only the hosted site has a service worker. The single-file bundle carries
  // its deck inline and has no sw.js to register.
  const bundled = !!window.__DECK_DATA__;
  if (!bundled && 'serviceWorker' in navigator && location.protocol === 'https:') {
    navigator.serviceWorker.register('sw.js').catch(() => { /* offline is a bonus, not a requirement */ });
  }
}

boot();
