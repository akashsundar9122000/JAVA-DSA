# JAVA-DSA

Solved Java data-structures and algorithms problems, plus a revision site generated from them.

**Live site:** https://akashsundar9122000.github.io/JAVA-DSA/

The site is built for revising away from the laptop — it installs to a phone home screen and
works with no signal once loaded. It reads the `.java` files in [`src/`](src/) and pulls out the
LeetCode id and link, the problem statement, the sample input/output, the constraints and your own
inline comments, then renders them with the solution and a spaced-repetition schedule.

`src/` is the single source of truth. Nothing in the build ever writes to it.

---

## Layout

```
src/          189 .java files across 21 topic folders — your solutions
tools/        the generator (zero dependencies, plain Node ESM)
docs/         the built site, served by GitHub Pages
```

## Rebuilding after you solve something new

```bash
npm run build          # re-reads src/, rewrites docs/data.json
git add -A && git commit -m "new problems" && git push
```

GitHub Pages redeploys itself on push. That's the whole loop.

Occasionally refresh the LeetCode metadata (official titles, difficulties, id→slug map):

```bash
npm run index          # the only script that touches the network
```

### Other commands

| command | what it does |
|---|---|
| `npm test` | 24 golden assertions over the corpus — run this if the parser ever looks wrong |
| `npm run check` | fails if any warning count grows beyond `tools/data/expected-warnings.json` |
| `npm run titles` | audit table: class name vs derived title vs official title, per file |
| `npm run stubs` | prints a paste-ready `overrides.json` skeleton for files missing metadata |
| `npm run serve` | serves `docs/` at http://localhost:8899 |
| `npm run icons` | regenerates the PWA icons |

## How the site reads your code

The generator lexes each file (a real character scanner, so braces inside strings and
commented-out code can't confuse it) and splits the comments into regions. Only comments between
the class's opening brace and its first member are treated as metadata:

```java
public class PowerOfKsubArrays {
    //3254, 3255                          <- problem ids
    //https://leetcode.com/problems/...    <- link (submission links count too)
    /**
     * You are given an array ...          <- statement, examples, constraints
     */
    static void main() { ... }             <- everything below is code
```

Anything in that header block that isn't an id, a link, a date or a status becomes **My notes** in
the UI, shown separately from the problem statement. A javadoc that opens with "How to approach?"
is scored as your notes rather than a statement, so your reasoning is never mistaken for
LeetCode's prose. Your notes are the most valuable thing in the repo; they get their own panel.

### Filling gaps without touching your source

Around a third of the files are drills and data-structure implementations with no LeetCode
problem behind them, so there is nothing to scrape. Those are described in
[`tools/data/overrides.json`](tools/data/overrides.json), keyed by file path. Anything supplied
from there is labelled **generated** in the UI so you can always tell it apart from your own
words — and your text always wins. Edit that file freely; `npm run stubs` prints a skeleton for
newly-added gap files.

## Two things the build noticed in your source

Both are one-character typos in id tags. The site uses the URL, which is correct in both cases, so
nothing is broken — but you may want to fix the comments:

- `src/Graph/CourseSchedule2.java` is tagged `//220`; Course Schedule II is **210**
- `src/Graph/FloodFill.java` is tagged `//773`; Flood Fill is **733**

## Using the site

`/` search (titles, ids, your notes, and the code itself) · `j`/`k` move · `Enter` open ·
`r` random problem · `f` flashcards · `1`/`2`/`3` grade Forgot/Shaky/Solid · `?` all shortcuts.

Grades are stored in your browser only — they never leave the device, so they don't follow you to
another phone and don't survive clearing site data. **Progress → Export** copies them out.

## Hosting

Pages serves the `docs/` folder on `main`. If it is ever switched off, re-enable it under
**Settings → Pages → Source: Deploy from a branch → `main` / `/docs`**.

To make rebuilds automatic instead of manual, a GitHub Actions workflow could run
`npm run build` and deploy on push; that needs **Settings → Pages → Source: GitHub Actions**.
The manual loop above is one command, so it isn't set up by default.
