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

const LS_KEY = 'dsa.deck.v2';
const LS_KEY_V1 = 'dsa.deck.v1';
const LOG_CAP = 2000;

const store = {
  data: { progress: {}, prefs: {}, log: [], watched: {} },
  ok: true,

  load() {
    try {
      const raw = localStorage.getItem(LS_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        this.data.progress = parsed.progress ?? {};
        this.data.prefs = parsed.prefs ?? {};
        this.data.log = Array.isArray(parsed.log) ? parsed.log : [];
        this.data.watched = parsed.watched ?? {};
        return;
      }

      // Migrate v1, which stored only the latest grade per problem and no
      // history. Seed one log entry per record from its `last` date so the
      // heatmap has something to show, and keep every grade intact.
      const old = localStorage.getItem(LS_KEY_V1);
      if (old) {
        const parsed = JSON.parse(old);
        this.data.progress = parsed.progress ?? {};
        this.data.prefs = parsed.prefs ?? {};
        this.data.log = Object.entries(this.data.progress)
          .filter(([, r]) => r?.last)
          .map(([id, r]) => ({ id, grade: r.grade ?? 3, at: r.last }));
        this.data.watched = {};
        this.save();
        this.migrated = true;
      }
    } catch { this.ok = false; }
  },

  save() {
    if (!this.ok) return;
    try { localStorage.setItem(LS_KEY, JSON.stringify(this.data)); } catch { this.ok = false; }
  },

  get(id) { return this.data.progress[id] ?? null; },

  /** Append a review to the history that drives the heatmap and streaks. */
  logReview(id, grade) {
    this.data.log.push({ id, grade, at: todayISO() });
    // Oldest entries fall off first; the heatmap only shows ~6 months anyway.
    if (this.data.log.length > LOG_CAP) this.data.log = this.data.log.slice(-LOG_CAP);
  },

  watch(id) {
    if (this.data.watched[id]) return;
    this.data.watched[id] = todayISO();
    this.save();
  },
  isWatched(id) { return !!this.data.watched[id]; },
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
  store.logReview(id, g);
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
  patternById: new Map(),
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

// --- pattern helpers -------------------------------------------------------

const patternOf = (p) => app.patternById.get(p.pattern) ?? null;
const patternName = (id) => app.patternById.get(id)?.name ?? id;

// --- video -----------------------------------------------------------------

const YT_THUMB = (id) => `https://i.ytimg.com/vi/${id}/hqdefault.jpg`;
const YT_SEARCH = (q) => `https://www.youtube.com/results?search_query=${encodeURIComponent(q)}`;

/**
 * A poster standing in for the iframe. Nothing loads from YouTube until the
 * play button is pressed, and only one player is ever mounted, so a long list
 * of problems stays cheap on a phone.
 */
function player(v, { watched = false } = {}) {
  const offline = !navigator.onLine;
  return `<div class="video">
    <div class="vframe" data-vid="${v.id}">
      ${offline ? `<div class="voffline">
          <b>Video needs a connection</b>
          <span>The rest of this page works offline.</span>
        </div>`
        : `<button class="vposter" type="button" data-act="play" data-vid="${v.id}" aria-label="Play: ${esc(v.title ?? 'explanation')}">
            <img src="${YT_THUMB(v.id)}" alt="" loading="lazy" decoding="async" width="480" height="360">
            <span class="vplay" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg></span>
            <span class="vlabel">
              <b>${esc(v.title ?? 'Explanation')}</b>
              <span>${esc(v.channel ?? 'YouTube')}${v.kind === 'concept' ? ' · topic explainer' : ''}</span>
            </span>
          </button>`}
    </div>
    <div class="vmeta">
      <span class="who">${esc(v.channel ?? 'YouTube')}</span>
      ${v.kind === 'concept' ? '<span class="chip">topic explainer, not this problem</span>' : ''}
      ${watched ? '<span class="chip">watched</span>' : ''}
      <a href="https://www.youtube.com/watch?v=${v.id}" target="_blank" rel="noopener">open on YouTube</a>
    </div>
  </div>`;
}

function videoBlock(p) {
  const pat = patternOf(p);
  const pl = pat?.playlist;
  const parts = [];

  if (p.video) {
    parts.push(player(p.video, { watched: store.isWatched(p.id) }));
  } else if (p.conceptVideo) {
    parts.push(player(p.conceptVideo, { watched: store.isWatched(p.id) }));
  } else {
    // No curated video for this one. Offer the pattern's playlist and a real
    // search — never a fabricated video id.
    parts.push(`<div class="empty" style="padding:18px">
      <h3>No curated video for this one</h3>
      <p>Nothing in the trusted channels maps to this problem specifically. The pattern playlist below covers the technique, or search YouTube directly.</p>
      <div class="setup-row" style="justify-content:center">
        <a class="bigbtn ghost" href="${YT_SEARCH(`${p.title} leetcode`)}" target="_blank" rel="noopener">Search YouTube</a>
        ${pl ? `<a class="bigbtn ghost" href="#/learn">Pattern playlist</a>` : ''}
      </div>
    </div>`);
  }

  if (pl) {
    parts.push(`<div class="vmeta" style="margin-top:10px">
      <span>Pattern course:</span>
      <a href="https://www.youtube.com/playlist?list=${pl.playlistId}" target="_blank" rel="noopener">${esc(pl.verifiedTitle ?? pat.name)}</a>
      <span class="kindtag ${pl.kind}">${pl.kind === 'dedicated' ? 'dedicated' : 'full course'}</span>
    </div>`);
  }

  return `<section class="block"><h2 class="block-h">Video explanation</h2>${parts.join('')}</section>`;
}

// --- leetcode enrichment ---------------------------------------------------

function hintsBlock(p) {
  if (!p.hints?.length) return '';
  return `<section class="block">
    <h2 class="block-h">Hints <span class="chip">${p.hints.length}</span></h2>
    <details class="hint" style="padding:0;border:0;background:none">
      <summary style="cursor:pointer;font:500 12px var(--mono);color:var(--accent);padding:4px 0">Reveal hints one at a time</summary>
      <div style="margin-top:8px">
        ${p.hints.map((hh, i) => `<details class="hint"><summary style="cursor:pointer"><span class="hint-n">Hint ${i + 1}</span></summary><div style="margin-top:6px">${esc(stripTags(hh))}</div></details>`).join('')}
      </div>
    </details>
  </section>`;
}

/** LeetCode hints arrive with HTML in them; render as text. */
function stripTags(html) {
  return String(html)
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ').replace(/&amp;/g, '&').replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&#39;/g, "'")
    .trim();
}

function tagsBlock(p) {
  if (!p.topicTags?.length) return '';
  return `<section class="block">
    <h2 class="block-h">LeetCode topics</h2>
    <div class="tagrow">${p.topicTags.map((t) => `<span class="tag">${esc(t)}</span>`).join('')}</div>
  </section>`;
}

function similarBlock(p) {
  if (!p.similar?.length) return '';
  const rows = p.similar.slice(0, 6).map((s) => {
    const mine = app.data.problems.find((x) => x.slug === s.slug);
    return mine
      ? `<a class="sim" href="#/p/${mine.id}">${esc(s.title)} ${diffChip(s.difficulty)}<span class="mine">in your repo →</span></a>`
      : `<a class="sim" href="https://leetcode.com/problems/${s.slug}/description/" target="_blank" rel="noopener">${esc(s.title)} ${diffChip(s.difficulty)}<span class="ext">not solved yet ↗</span></a>`;
  }).join('');
  return `<section class="block"><h2 class="block-h">Similar problems</h2><div class="simlist">${rows}</div></section>`;
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
      <a class="chip" href="#/pattern/${p.pattern}">${esc(patternName(p.pattern))}</a>
      ${p.acRate ? `<span class="chip" title="LeetCode acceptance rate">${esc(p.acRate)} accepted</span>` : ''}
      ${p.lists?.blind75 ? '<span class="chip">Blind 75</span>' : ''}
      ${p.lists?.neetcode150 && !p.lists?.blind75 ? '<span class="chip">NeetCode 150</span>' : ''}
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
    ${hintsBlock(p)}
    ${notesBlock(p)}
    ${teachesBlock(p)}
    ${videoBlock(p)}

    <section class="block">
      <h2 class="block-h">Solution</h2>
      ${codeBlock(p, { bodyOnly: !!store.pref('bodyOnly') })}
    </section>

    ${altBlock(p)}
    ${similarBlock(p)}
    ${tagsBlock(p)}

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
  else if (scope && scope !== 'all') {
    // A scope is a pattern id or, for older links, a folder id.
    pool = app.patternById.has(scope)
      ? all.filter((p) => p.pattern === scope)
      : all.filter((p) => p.category === scope);
  } else pool = all;

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
      <span>${esc(s.scope === 'due' ? 'due queue' : s.scope === 'all' ? 'all topics' : (app.patternById.get(s.scope)?.name ?? catName(s.scope)))}</span>
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

/* ---- pattern browse ------------------------------------------------------ */

function masteryOf(items) {
  const c = { solid: 0, shaky: 0, forgot: 0 };
  for (const p of items) { const st = statusOf(p.id); if (st) c[st]++; }
  c.none = items.length - c.solid - c.shaky - c.forgot;
  c.total = items.length;
  return c;
}

function miniBar(c) {
  if (!c.total) return '';
  const w = (n) => `${(n / c.total) * 100}%`;
  return `<span class="mini">
    ${c.solid ? `<i class="solid" style="width:${w(c.solid)}"></i>` : ''}
    ${c.shaky ? `<i class="shaky" style="width:${w(c.shaky)}"></i>` : ''}
    ${c.forgot ? `<i class="forgot" style="width:${w(c.forgot)}"></i>` : ''}
  </span>`;
}

function playlistLine(pt) {
  const pl = pt.playlist;
  if (!pl) return '';
  return `<div class="vmeta">
    <a href="https://www.youtube.com/playlist?list=${pl.playlistId}" target="_blank" rel="noopener">${esc(pl.verifiedTitle ?? pt.name)}</a>
    <span class="who">${esc(pl.verifiedChannel ?? pl.channel ?? '')}</span>
    <span class="kindtag ${pl.kind}">${pl.kind === 'dedicated' ? 'dedicated' : 'full course'}</span>
  </div>`;
}

function viewPattern(id) {
  const pt = app.patternById.get(id);
  if (!pt) return `<div class="wrap"><div class="empty"><h3>Unknown pattern</h3><p>No pattern with id <code>${esc(id ?? '')}</code>.</p><a class="bigbtn ghost" href="#/learn">All patterns</a></div></div>`;

  const items = app.data.problems.filter((p) => p.pattern === id);
  app.listing = items;
  const c = masteryOf(items);

  return `<div class="wrap">
    <div class="pagehead"><span class="eyebrow">Algorithm pattern</span></div>
    <div class="pagehead">
      <h1>${esc(pt.name)}</h1>
      <p>${plural(items.length, 'problem')} · ${c.solid} solid${c.none ? ` · ${c.none} untouched` : ''}</p>
    </div>

    <p class="prose dim" style="margin:10px 0 0;max-width:66ch">${esc(pt.blurb)}</p>
    ${miniBar(c)}
    ${playlistLine(pt)}

    <div class="setup-row" style="margin-top:14px">
      <a class="bigbtn" href="#/cards/${id}">Drill this pattern</a>
      <a class="bigbtn ghost" href="#/learn">Playlists</a>
    </div>

    <div class="section">
      <div class="section-head">
        <h2>Problems</h2>
        <span class="count">${pt.counts.withVideo} with a video</span>
      </div>
      ${problemList(items)}
    </div>
  </div>`;
}

/* ---- learn: a playlist per pattern --------------------------------------- */

function viewLearn() {
  const cards = (app.data.patterns ?? []).map((pt) => {
    const items = app.data.problems.filter((p) => p.pattern === pt.id);
    const c = masteryOf(items);
    const pl = pt.playlist;

    return `<article class="plcard">
      <div class="plcard-head">
        <div class="plcard-top">
          <h3>${esc(pt.name)}</h3>
          <span class="plcard-n">${plural(items.length, 'problem')}</span>
        </div>
        <p class="plcard-blurb">${esc(pt.blurb)}</p>
        ${miniBar(c)}
        <p class="plcard-n" style="margin:0">${c.solid} solid · ${c.shaky + c.forgot} needs work · ${c.none} untouched</p>
      </div>

      <div class="plcard-body">
        ${pl ? `<div class="vframe" data-vid="pl-${pt.id}">
            <button class="vposter" type="button" data-act="play-list" data-pl="${pl.playlistId}" aria-label="Play playlist: ${esc(pl.verifiedTitle ?? pt.name)}">
              ${pl.posterVideoId ? `<img src="${YT_THUMB(pl.posterVideoId)}" alt="" loading="lazy" decoding="async" width="480" height="360">` : ''}
              <span class="vplay" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg></span>
              <span class="vlabel">
                <b>${esc(pl.verifiedTitle ?? pt.name)}</b>
                <span>${esc(pl.verifiedChannel ?? pl.channel ?? '')}</span>
              </span>
            </button>
          </div>
          <p class="plcard-why" style="margin-top:9px">${esc(pl.why ?? '')}</p>`
        : '<p class="plcard-why">No curated playlist for this pattern yet.</p>'}
      </div>

      <div class="plcard-foot">
        ${pl ? `<span class="kindtag ${pl.kind}">${pl.kind === 'dedicated' ? 'dedicated playlist' : 'full course'}</span>` : ''}
        <a class="extlink" href="#/pattern/${pt.id}">Problems</a>
        <a class="extlink" href="#/cards/${pt.id}">Drill</a>
        ${pl ? `<a class="extlink" href="https://www.youtube.com/playlist?list=${pl.playlistId}" target="_blank" rel="noopener">
          <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M11 4h5v5M16 4l-7 7M8 5H4v11h11v-4"/></svg>YouTube</a>` : ''}
      </div>
    </article>`;
  }).join('');

  const ded = (app.data.patterns ?? []).filter((p) => p.playlist?.kind === 'dedicated').length;

  return `<div class="wrap">
    <div class="pagehead"><span class="eyebrow">Curated · NeetCode · take U forward · Aditya Verma · Kunal Kushwaha</span></div>
    <div class="pagehead">
      <h1>Learn by pattern</h1>
      <p>${app.data.patterns.length} patterns · ${ded} with a dedicated playlist</p>
    </div>
    <p class="prose dim" style="margin:10px 0 0;max-width:66ch">Every playlist here was resolved against YouTube and is labelled with what it actually is: a playlist dedicated to the pattern, or a full course that covers it among other topics.</p>
    <div class="section"><div class="learngrid">${cards}</div></div>
  </div>`;
}

/* ---- dashboard ----------------------------------------------------------- */

/** Reviews per day, from the log. */
function activityByDay() {
  const map = new Map();
  for (const e of store.data.log ?? []) {
    if (!e?.at) continue;
    map.set(e.at, (map.get(e.at) ?? 0) + 1);
  }
  return map;
}

function streaks(byDay) {
  const days = [...byDay.keys()].sort();
  if (!days.length) return { current: 0, longest: 0 };

  let longest = 1;
  let run = 1;
  for (let i = 1; i < days.length; i++) {
    run = daysBetween(days[i - 1], days[i]) === 1 ? run + 1 : 1;
    if (run > longest) longest = run;
  }

  // A streak stays alive if the last review was today or yesterday.
  const last = days[days.length - 1];
  const gap = daysBetween(last, todayISO());
  let current = 0;
  if (gap <= 1) {
    current = 1;
    for (let i = days.length - 1; i > 0; i--) {
      if (daysBetween(days[i - 1], days[i]) === 1) current++;
      else break;
    }
  }
  return { current, longest };
}

/**
 * 26-week activity grid. Sequential single-hue ramp with monotonic lightness
 * (see --heat-* tokens), so density reads correctly in both themes and for
 * colour-vision deficiencies.
 */
function heatmap(byDay) {
  const WEEKS = 26;
  const CELL = 13;
  const GAP = 3;
  const LEFT = 26;
  const TOP = 16;

  // Start on the Sunday that begins the window.
  const end = new Date(todayISO() + 'T00:00:00');
  const start = new Date(end);
  start.setDate(start.getDate() - (WEEKS * 7 - 1));
  start.setDate(start.getDate() - start.getDay());

  const max = Math.max(1, ...byDay.values());
  const step = (n) => (n === 0 ? 0 : n >= max ? 4 : 1 + Math.floor((n / max) * 3));

  const w = LEFT + WEEKS * (CELL + GAP);
  const h = TOP + 7 * (CELL + GAP) + 14;

  let cells = '';
  let months = '';
  let lastMonth = -1;

  for (let wk = 0; wk < WEEKS; wk++) {
    for (let d = 0; d < 7; d++) {
      const day = new Date(start);
      day.setDate(day.getDate() + wk * 7 + d);
      if (day > end) continue;
      const iso = day.toISOString().slice(0, 10);
      const n = byDay.get(iso) ?? 0;
      const x = LEFT + wk * (CELL + GAP);
      const y = TOP + d * (CELL + GAP);
      cells += `<rect x="${x}" y="${y}" width="${CELL}" height="${CELL}" fill="var(--heat-${step(n)})"><title>${iso}: ${plural(n, 'review')}</title></rect>`;

      if (d === 0) {
        const m = day.getMonth();
        if (m !== lastMonth) {
          lastMonth = m;
          months += `<text x="${x}" y="${TOP - 5}">${day.toLocaleString('en', { month: 'short' })}</text>`;
        }
      }
    }
  }

  const dayLabels = ['Mon', 'Wed', 'Fri']
    .map((lab, i) => `<text x="0" y="${TOP + (i * 2 + 1) * (CELL + GAP) + 10}">${lab}</text>`).join('');

  return `<div class="heatwrap">
    <svg class="heat" viewBox="0 0 ${w} ${h}" width="${w}" height="${h}" role="img"
         aria-label="Reviews per day over the last 26 weeks">
      ${months}${dayLabels}${cells}
    </svg>
  </div>`;
}

function viewDash() {
  const all = app.data.problems.filter((p) => p.kind !== 'scratch');
  const byDay = activityByDay();
  const st = streaks(byDay);
  const c = masteryOf(all);
  const reps = (store.data.log ?? []).length;

  // --- mastery, weakest pattern first ---
  const rows = (app.data.patterns ?? []).map((pt) => {
    const items = app.data.problems.filter((p) => p.pattern === pt.id);
    const m = masteryOf(items);
    // Weakness = how little of the pattern is solid, weighted by how much of
    // it you have actually attempted, so a big untouched pattern outranks a
    // small one you have half-learned.
    const score = items.length ? (m.solid / items.length) : 1;
    return { pt, m, score };
  }).sort((a, b) => a.score - b.score || b.m.total - a.m.total);

  const w = (n, t) => `${(n / t) * 100}%`;
  const mastery = rows.map(({ pt, m }, i) => `<a class="mrow${i < 3 ? ' weak' : ''}" href="#/pattern/${pt.id}">
    <span class="mrow-k">${esc(pt.name)}</span>
    <span class="mrow-bar">
      ${m.solid ? `<i class="solid" style="width:${w(m.solid, m.total)}"></i>` : ''}
      ${m.shaky ? `<i class="shaky" style="width:${w(m.shaky, m.total)}"></i>` : ''}
      ${m.forgot ? `<i class="forgot" style="width:${w(m.forgot, m.total)}"></i>` : ''}
    </span>
    <span class="mrow-n"><b>${m.solid}</b>/${m.total}</span>
  </a>`).join('');

  // --- Blind 75 / NeetCode 150 ---
  const listBlock = (key, label, total) => {
    const mine = all.filter((p) => p.lists?.[key]);
    const solid = mine.filter((p) => statusOf(p.id) === 'solid').length;
    const seen = mine.filter((p) => store.get(p.id)).length;
    const untouched = mine.filter((p) => !store.get(p.id));
    return `<div class="listgroup">
      <div class="listrow">
        <h3>${label}</h3>
        <span class="frac">${mine.length} of ${total} in your repo</span>
      </div>
      <div class="track">
        <i class="done" style="width:${w(solid, total)}"></i>
        <i class="part" style="width:${w(Math.max(0, seen - solid), total)}"></i>
      </div>
      <p class="listnote">${solid} solid · ${seen - solid} reviewed but not solid · ${total - mine.length} not in your repo yet${untouched.length ? ` · <a href="#/p/${untouched[0].id}">start with ${esc(untouched[0].title)}</a>` : ''}</p>
    </div>`;
  };

  // --- coverage ---
  const withVideo = all.filter((p) => p.video).length;
  const watched = all.filter((p) => store.isWatched(p.id)).length;
  const noNotes = all.filter((p) => !p.notes.length && !p.authorNotes.length);
  const noLink = all.filter((p) => !p.hasCanonicalUrl);
  const never = all.filter((p) => !store.get(p.id));
  const diff = {
    e: all.filter((p) => p.difficulty === 'Easy').length,
    m: all.filter((p) => p.difficulty === 'Medium').length,
    h: all.filter((p) => p.difficulty === 'Hard').length,
  };
  const diffTotal = diff.e + diff.m + diff.h || 1;

  const prof = app.data.profile;

  return `<div class="wrap">
    <div class="pagehead"><span class="eyebrow">${all.length} files · ${app.data.patterns.length} patterns</span></div>
    <div class="pagehead">
      <h1>Progress</h1>
      <p>${reps ? `${plural(reps, 'review')} logged` : 'No reviews logged yet — grade a problem and this fills in.'}</p>
    </div>

    <div class="statstrip" style="margin-top:14px">
      <div class="stat"><span class="stat-k">Solid</span><span class="stat-v" style="color:var(--good)">${c.solid}</span></div>
      <div class="stat"><span class="stat-k">Needs work</span><span class="stat-v" style="color:var(--warn)">${c.shaky + c.forgot}</span></div>
      <div class="stat"><span class="stat-k">Untouched</span><span class="stat-v">${c.none}</span></div>
      <div class="stat is-due"><span class="stat-k">Due now</span><span class="stat-v">${all.filter((p) => isDue(p.id)).length}</span></div>
    </div>

    <div class="dashgrid" style="margin-top:18px">

      <section class="panel">
        <div class="panel-head">
          <h2>Pattern mastery</h2>
          <p>weakest first</p>
          <span class="grow">${c.solid}/${c.total} solid overall</span>
        </div>
        <div class="panel-body">
          <div class="chartlegend" style="margin-bottom:12px">
            <span><i class="sw-solid"></i>Solid</span>
            <span><i class="sw-shaky"></i>Shaky</span>
            <span><i class="sw-forgot"></i>Forgot</span>
            <span><i class="sw-none"></i>Not reviewed</span>
          </div>
          <div class="mastery">${mastery}</div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h2>Review activity</h2>
          <p>last 26 weeks</p>
        </div>
        <div class="panel-body">
          <div class="streaks">
            <div class="streak${st.current ? ' on' : ''}">
              <div class="streak-v">${st.current}</div>
              <div class="streak-k">Day streak</div>
            </div>
            <div class="streak">
              <div class="streak-v">${st.longest}</div>
              <div class="streak-k">Longest</div>
            </div>
            <div class="streak">
              <div class="streak-v">${byDay.get(todayISO()) ?? 0}</div>
              <div class="streak-k">Today</div>
            </div>
            <div class="streak">
              <div class="streak-v">${byDay.size}</div>
              <div class="streak-k">Active days</div>
            </div>
          </div>
          ${heatmap(byDay)}
          <div class="heatfoot">
            <span>less</span>
            <span class="heatscale">
              <i style="background:var(--heat-0)"></i><i style="background:var(--heat-1)"></i>
              <i style="background:var(--heat-2)"></i><i style="background:var(--heat-3)"></i>
              <i style="background:var(--heat-4)"></i>
            </span>
            <span>more</span>
            ${byDay.size === 0 ? '<span style="margin-left:auto">Nothing logged yet — grading a problem marks today.</span>' : ''}
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h2>Interview lists</h2>
          <p>what actually gets asked</p>
        </div>
        <div class="panel-body">
          ${listBlock('blind75', 'Blind 75', 75)}
          ${listBlock('neetcode150', 'NeetCode 150', 150)}
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h2>Coverage</h2>
          <p>gaps worth closing</p>
        </div>
        <div class="panel-body">
          <div class="covgrid">
            <a class="cov" href="#/browse">
              <div class="cov-v">${watched}<span class="cov-s">/${withVideo}</span></div>
              <div class="cov-k">Videos watched</div>
            </a>
            <a class="cov" href="#/cards">
              <div class="cov-v">${never.length}</div>
              <div class="cov-k">Never reviewed</div>
            </a>
            <div class="cov">
              <div class="cov-v">${noNotes.length}</div>
              <div class="cov-k">No notes of yours</div>
            </div>
            <div class="cov">
              <div class="cov-v">${noLink.length}</div>
              <div class="cov-k">No problem link</div>
            </div>
          </div>

          <div style="margin-top:14px">
            <div class="listrow"><h3>Difficulty mix</h3><span class="frac">${diffTotal} rated</span></div>
            <div class="diffbar">
              <i class="e" style="width:${w(diff.e, diffTotal)}"></i>
              <i class="m" style="width:${w(diff.m, diffTotal)}"></i>
              <i class="h" style="width:${w(diff.h, diffTotal)}"></i>
            </div>
            <p class="listnote">${diff.e} easy · ${diff.m} medium · ${diff.h} hard</p>
          </div>
        </div>
      </section>

      ${prof ? `<section class="panel">
        <div class="panel-head">
          <h2>LeetCode profile</h2>
          <p>${esc(prof.username)}</p>
          <span class="grow">fetched ${esc((prof.fetchedAt ?? '').slice(0, 10))}</span>
        </div>
        <div class="panel-body">
          <div class="covgrid">
            <div class="cov"><div class="cov-v">${prof.solved?.all ?? '—'}</div><div class="cov-k">Solved total</div></div>
            <div class="cov"><div class="cov-v" style="color:var(--easy)">${prof.solved?.easy ?? '—'}</div><div class="cov-k">Easy</div></div>
            <div class="cov"><div class="cov-v" style="color:var(--medium)">${prof.solved?.medium ?? '—'}</div><div class="cov-k">Medium</div></div>
            <div class="cov"><div class="cov-v" style="color:var(--hard)">${prof.solved?.hard ?? '—'}</div><div class="cov-k">Hard</div></div>
            ${prof.ranking ? `<div class="cov"><div class="cov-v">${Number(prof.ranking).toLocaleString('en')}</div><div class="cov-k">Global rank</div></div>` : ''}
            ${prof.contest?.rating ? `<div class="cov"><div class="cov-v">${Math.round(prof.contest.rating)}</div><div class="cov-k">Contest rating</div></div>` : ''}
          </div>
          <p class="listnote">Public profile data, refreshed at build time by <code>npm run profile</code> — not live.</p>
        </div>
      </section>` : ''}

      <section class="panel">
        <div class="panel-head"><h2>Your progress data</h2></div>
        <div class="panel-body">
          <p style="color:var(--text-dim);font-size:13px;margin:0 0 12px">
            ${store.ok
              ? 'Grades live in this browser only — they never leave your device, so they do not follow you to another phone or survive clearing site data. Export a copy to keep them.'
              : 'This browser is blocking local storage, so grades cannot be saved this session. Everything else works.'}
          </p>
          <div class="setup-row">
            <button class="bigbtn ghost" type="button" data-act="export">Export progress</button>
            <button class="bigbtn ghost" type="button" data-act="import">Import progress</button>
            <button class="bigbtn ghost" type="button" data-act="reset">Reset all</button>
          </div>
        </div>
      </section>

    </div>
  </div>`;
}

/* ================================================================= render == */

function renderRail() {
  const mode = store.pref('railMode') ?? 'patterns';
  const r = app.route;

  const seg = `<div class="railseg" role="group" aria-label="Group problems by">
    <button type="button" data-act="rail-mode" data-mode="patterns" aria-pressed="${mode === 'patterns'}">Patterns</button>
    <button type="button" data-act="rail-mode" data-mode="folders" aria-pressed="${mode === 'folders'}">Folders</button>
  </div>`;

  const allRow = (href, label, n, current) => `<a class="catrow" href="${href}" aria-current="${current}">
      <span class="catrow-name">${label}</span>
      <span class="catrow-n">${n}</span>
    </a>`;

  let rows;
  if (mode === 'patterns') {
    const cur = r.name === 'pattern' ? r.arg
      : r.name === 'problem' ? app.byId.get(r.arg)?.pattern : null;

    rows = (app.data.patterns ?? []).map((pt) => {
      const items = app.data.problems.filter((x) => x.pattern === pt.id);
      const solid = items.filter((x) => statusOf(x.id) === 'solid').length;
      const pct = items.length ? (solid / items.length) * 100 : 0;
      return `<a class="catrow" href="#/pattern/${pt.id}" aria-current="${cur === pt.id}">
        <span class="catrow-name">${esc(pt.name)}</span>
        <span class="catrow-n">${pt.counts.files}</span>
        ${solid ? `<span class="catrow-meter"><i style="width:${pct}%"></i></span>` : ''}
      </a>`;
    }).join('');
  } else {
    // 'root' holds only the IDE scratch files; they stay reachable by search
    // and direct link, but they are not a topic worth a rail row.
    const cats = app.data.categories.filter((c) => c.counts.files > 0 && c.id !== 'root');
    const cur = r.name === 'browse' ? r.arg
      : r.name === 'problem' ? app.byId.get(r.arg)?.category : null;

    rows = cats.map((c) => {
      const items = app.data.problems.filter((x) => x.category === c.id);
      const solid = items.filter((x) => statusOf(x.id) === 'solid').length;
      const pct = items.length ? (solid / items.length) * 100 : 0;
      return `<a class="catrow${c.depth > 1 ? ' sub' : ''}" href="#/browse/${c.id}" aria-current="${cur === c.id}">
        <span class="catrow-name">${esc(c.name)}</span>
        <span class="catrow-n">${c.counts.files}</span>
        ${solid ? `<span class="catrow-meter"><i style="width:${pct}%"></i></span>` : ''}
      </a>`;
    }).join('');
  }

  const total = app.data.problems.filter((x) => x.kind !== 'scratch').length;
  el('#rail-list').innerHTML = seg
    + `<div class="rail-head">${mode === 'patterns' ? 'Algorithm patterns' : 'Repo folders'}</div>`
    + allRow('#/browse', 'All problems', total, r.name === 'browse' && !r.arg)
    + rows;
}

function render() {
  const main = el('#main');
  const r = app.route;

  if (r.name === 'today') main.innerHTML = viewToday();
  else if (r.name === 'browse') main.innerHTML = viewBrowse(r.arg);
  else if (r.name === 'problem') main.innerHTML = viewProblem(r.arg);
  else if (r.name === 'pattern') main.innerHTML = viewPattern(r.arg);
  else if (r.name === 'cards') main.innerHTML = viewCards(r.arg);
  else if (r.name === 'learn') main.innerHTML = viewLearn();
  else if (r.name === 'dash') main.innerHTML = viewDash();

  renderRail();

  // tab + badge state
  const due = app.data.problems.filter((p) => p.kind !== 'scratch' && isDue(p.id)).length;
  el('#tab-due').dataset.n = String(due);
  document.querySelectorAll('.tabbar a').forEach((a) => {
    const owns = a.dataset.tab === r.name
      || (r.name === 'problem' && a.dataset.tab === 'browse')
      || (r.name === 'pattern' && a.dataset.tab === 'browse');
    a.setAttribute('aria-current', String(owns));
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
  if (head === 'pattern') return { name: 'pattern', arg: tail || null };
  if (head === 'cards') return { name: 'cards', arg: tail || null };
  if (head === 'learn') return { name: 'learn', arg: tail || null };
  // 'stats' kept as an alias so old links and bookmarks still resolve.
  if (head === 'dash' || head === 'stats') return { name: 'dash', arg: null };
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
  const payload = JSON.stringify({
    kind: 'dsa-deck-progress', version: 2, exported: new Date().toISOString(),
    progress: store.data.progress, log: store.data.log, watched: store.data.watched,
  }, null, 2);
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
  ['g then l', 'Go to Learn'],
  ['g then d', 'Go to Progress'],
  ['v', 'Play the video on a problem'],
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
    case 'rail-mode':
      store.pref('railMode', t.dataset.mode);
      renderRail();
      break;

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

    case 'play': {
      const vid = t.dataset.vid;
      // Tear down any other player first — one iframe at a time, by design.
      document.querySelectorAll('.vframe iframe').forEach((f) => f.remove());
      const frame = t.closest('.vframe');
      const iframe = document.createElement('iframe');
      // nocookie host, and no related-video grid at the end.
      iframe.src = `https://www.youtube-nocookie.com/embed/${vid}?autoplay=1&rel=0&modestbranding=1`;
      iframe.title = 'Video explanation';
      iframe.allow = 'accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture';
      iframe.allowFullscreen = true;
      iframe.referrerPolicy = 'strict-origin-when-cross-origin';
      frame.replaceChildren(iframe);
      if (pid) store.watch(pid);
      break;
    }

    case 'play-list': {
      document.querySelectorAll('.vframe iframe').forEach((f) => f.remove());
      const frame = t.closest('.vframe');
      const iframe = document.createElement('iframe');
      iframe.src = `https://www.youtube-nocookie.com/embed/videoseries?list=${t.dataset.pl}&rel=0&modestbranding=1`;
      iframe.title = 'Playlist';
      iframe.allow = 'accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture';
      iframe.allowFullscreen = true;
      iframe.referrerPolicy = 'strict-origin-when-cross-origin';
      frame.replaceChildren(iframe);
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
        // A v1 export has no history; rebuild what we can from `last` dates.
        store.data.log = Array.isArray(parsed.log) ? parsed.log
          : Object.entries(incoming).filter(([, r]) => r?.last).map(([id, r]) => ({ id, grade: r.grade ?? 3, at: r.last }));
        store.data.watched = parsed.watched ?? {};
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
    if (e.key === 'l') { location.hash = '#/learn'; return; }
    if (e.key === 'd' || e.key === 's') { location.hash = '#/dash'; return; }
  }

  switch (e.key) {
    case '/': e.preventDefault(); el('#q').focus(); el('#q').select(); break;
    case '?': helpSheet(); break;
    case 'g': gPending = true; break;
    case 'j': e.preventDefault(); moveCursor(1); break;
    case 'k': e.preventDefault(); moveCursor(-1); break;
    case 'r': randomProblem(); break;
    case 'v': {
      const poster = el('.vposter');
      if (poster) { e.preventDefault(); poster.click(); }
      break;
    }
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
  for (const pt of app.data.patterns ?? []) app.patternById.set(pt.id, pt);
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
