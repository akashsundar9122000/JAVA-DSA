# JAVA-DSA

Solved Java data-structures and algorithms problems, plus a revision site generated from them.

**Live site:** https://akashsundar9122000.github.io/JAVA-DSA/

The site is built for revising away from the laptop — it installs to a phone home screen and
works with no signal once loaded. It reads the `.java` files in [`src/`](src/) and pulls out the
LeetCode id and link, the problem statement, the sample input/output, the constraints and your own
inline comments, then renders them with the solution and a spaced-repetition schedule.

It also organises everything by **algorithm pattern**, embeds a **video explanation** per problem,
carries a **curated playlist per pattern**, and tracks progress on a **dashboard**.

`src/` is the single source of truth. Nothing in the build ever writes to it.

---

## Layout

```
src/          189 .java files across 21 topic folders — your solutions
tools/        the generator (zero dependencies, plain Node ESM)
docs/         the built site, served by GitHub Pages
```

## Patterns, video and the dashboard

Problems are grouped into 23 canonical patterns (NeetCode's taxonomy, extended with Prefix Sum,
Sorting, Queue, Recursion and Data Structures so every file lands somewhere honest). A problem's
pattern is resolved by a cascade, and `patternSource` records which rung fired:

1. NeetCode's own pattern, for problems in their curated set
2. otherwise LeetCode's real topic tags, priority-ordered so `['Array','Sliding Window']`
   resolves to Sliding Window rather than the near-meaningless Array
3. otherwise your folder name

**Video** comes in three tiers, and the distinction is shown in the UI rather than blurred:

| tier | source | count |
|---|---|---|
| exact solution walkthrough | NeetCode's curated problem→video map | 70 |
| searched, channel-restricted | YouTube Data API, needs your key | the rest |
| pattern playlist | hand-curated, one per pattern | 23 |

**Every YouTube id is verified before it ships.** `npm run verify` resolves each video through
YouTube's oEmbed endpoint and each playlist through its RSS feed, keeps the real title and
channel that come back, and drops anything that 404s. A problem with no confident match shows a
*Search YouTube* button — never a broken embed. `npm test` fails the build if an unverified id
ever slips into `docs/data.json`.

Playlists are only accepted from four channels: NeetCode, take U forward, Aditya Verma and
Kunal Kushwaha. Third-party mirror playlists resolve fine but are rejected on purpose — they can
be reordered or deleted by someone with no stake in them. Each is labelled **dedicated playlist**
or **full course**, so a course that merely covers a topic is never presented as a playlist about it.

### Two things worth knowing

- **Video needs a connection.** Thumbnails and metadata are cached for offline use, so the cards
  still render and the player says it needs signal. Statements, code, your notes, flashcards and
  the dashboard all work fully offline.
- **The Claude Artifact copy cannot embed video.** Its content policy blocks third-party iframes
  and images, so there the videos degrade to links. The GitHub Pages site is the full experience.

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
| `npm run harvest` | the whole data pipeline: neetcode → leetcode → videos → verify → build |
| `npm run neetcode` | refresh the NeetCode problem→video map (no key) |
| `npm run leetcode` | refresh topic tags, hints, similar problems, acceptance rates (no key) |
| `npm run videos` | search for the missing per-problem videos (**needs a YouTube API key**) |
| `npm run playlists` | upgrade "full course" patterns to dedicated playlists (**needs the key**) |
| `npm run profile` | refresh your public LeetCode stats (**needs your username**) |
| `npm run verify` | re-check every video and playlist id still resolves |

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

## Optional: your API key and LeetCode username

Both are optional. Without them the site still ships 70 verified videos, all 23 playlists, full
pattern grouping and the whole dashboard — the harvesters just skip with a message.

```bash
cp tools/data/secrets.example.json tools/data/secrets.json
# then fill in:
#   youtubeApiKey  console.cloud.google.com → new project → enable
#                  "YouTube Data API v3" → Credentials → API key. No billing card.
#   leetcodeUser   the handle in your profile URL, leetcode.com/u/<handle>
npm run videos && npm run playlists && npm run profile && npm run build
```

`tools/data/secrets.json` is gitignored and never reaches this public repo. The *derived* data
does get committed and published, including your public LeetCode stats — delete
`tools/data/profile.json` and rebuild to remove them.

`search.list` costs 100 of a free 10,000 units per day, so roughly 100 searches daily; the
harvester prints its spend and stops before exceeding the budget.

## A note on the MCP servers

You linked `ia-programming/youtube-mcp` and `jinzcdev/leetcode-mcp-server`. Neither is needed
here, for a structural reason: this is a **static site**, so it can never call an MCP server at
runtime — MCP could only ever be a build-time harvesting tool. Both servers are also thin wrappers
over endpoints the scripts in `tools/` already call directly:

| need | what we call | key required |
|---|---|---|
| topic tags, hints, similar, acceptance rate | `leetcode.com/graphql` | no |
| public profile and contest rating | same endpoint, `matchedUser` | no |
| does this video id exist, and what is it really called | `youtube.com/oembed` | no |
| does this playlist exist | `youtube.com/feeds/videos.xml` | no |
| finding new videos | YouTube Data API v3 | yes |

Committed scripts are also strictly better than an MCP session: you can re-run them yourself,
forever, without needing a particular tool attached.

## Hosting

Pages serves the `docs/` folder on `main`. If it is ever switched off, re-enable it under
**Settings → Pages → Source: Deploy from a branch → `main` / `/docs`**.

To make rebuilds automatic instead of manual, a GitHub Actions workflow could run
`npm run build` and deploy on push; that needs **Settings → Pages → Source: GitHub Actions**.
The manual loop above is one command, so it isn't set up by default.
