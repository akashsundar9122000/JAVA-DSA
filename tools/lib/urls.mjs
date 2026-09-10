// URL canonicalization.
//
// Everything is kept: `urls[]` holds every raw link with its role, while
// `canonicalUrl` is the single best "go read the problem" target.

const LC_PROBLEM = /^https?:\/\/(?:www\.)?leetcode\.com\/problems\/([^/?#]+)/i;
const LC_CONTEST = /^https?:\/\/(?:www\.)?leetcode\.com\/contest\/([^/]+)\/problems\/([^/?#]+)/i;
const LC_SUBMISSION = /\/submissions\/(\d+)/;
const LC_SOLUTION = /\/solutions\//;
const GFG_BATCH = /^https?:\/\/(?:www\.)?geeksforgeeks\.org\/batch\/[^/]+\/track\/[^/]+\/problem\/([^/?#]+)/i;
const GFG_PROBLEM = /^https?:\/\/(?:www\.)?geeksforgeeks\.org\/problems\/([^/?#]+)/i;
const GFG_ARTICLE = /^https?:\/\/(?:www\.)?geeksforgeeks\.org\/(?:dsa\/)?([^/?#]+)/i;
const HACKERRANK = /^https?:\/\/(?:www\.)?hackerrank\.com\/challenges\/([^/?#]+)/i;

/**
 * @param {string} raw
 * @returns {{raw,canonical,host,role,slug,submissionId,contest,envId}}
 */
export function classifyUrl(raw) {
  const url = raw.replace(/[.,;)]+$/, '');
  const envId = url.match(/[?&]envId=([^&]+)/)?.[1] ?? null;
  const bare = url.split(/[?#]/)[0];

  let m;

  if ((m = bare.match(LC_CONTEST))) {
    // Contest problems graduate to the archive under the same slug; the
    // contest URL 404s for non-participants, so canonicalize but keep both.
    return {
      raw, host: 'leetcode.com', role: 'contest', slug: m[2],
      canonical: `https://leetcode.com/problems/${m[2]}/description/`,
      contest: m[1], submissionId: null, envId,
    };
  }

  if ((m = bare.match(LC_PROBLEM))) {
    const slug = m[1];
    const sub = bare.match(LC_SUBMISSION);
    const role = sub ? 'submission' : LC_SOLUTION.test(bare) ? 'solution' : 'problem';
    return {
      raw, host: 'leetcode.com', role, slug,
      canonical: `https://leetcode.com/problems/${slug}/description/`,
      contest: null, submissionId: sub ? sub[1] : null, envId,
    };
  }

  if ((m = bare.match(GFG_BATCH))) {
    return {
      raw, host: 'geeksforgeeks.org', role: 'problem', slug: m[1],
      canonical: `https://www.geeksforgeeks.org/problems/${m[1]}/1`,
      contest: null, submissionId: null, envId,
    };
  }

  if ((m = bare.match(GFG_PROBLEM))) {
    return {
      raw, host: 'geeksforgeeks.org', role: 'problem', slug: m[1],
      canonical: bare, contest: null, submissionId: null, envId,
    };
  }

  if ((m = bare.match(HACKERRANK))) {
    return {
      raw, host: 'hackerrank.com', role: 'problem', slug: m[1],
      canonical: bare, contest: null, submissionId: null, envId,
    };
  }

  if ((m = bare.match(GFG_ARTICLE))) {
    return {
      raw, host: 'geeksforgeeks.org', role: 'article', slug: m[1],
      canonical: bare, contest: null, submissionId: null, envId,
    };
  }

  let host = 'unknown';
  try { host = new URL(bare).hostname.replace(/^www\./, ''); } catch { /* keep unknown */ }
  return { raw, host, role: 'unknown', slug: null, canonical: bare, contest: null, submissionId: null, envId };
}

/**
 * Pick the best canonical problem link, and record where it came from.
 *
 * A LeetCode problem URL cannot be built from a numeric id — /problems/<id>
 * is a 404 — so when only an id is known we either resolve it through the
 * committed slug index or fall back to a search link that is visibly a search
 * link. We never fabricate a problem URL.
 */
export function resolveCanonical(classified, ids, slugIndex) {
  const byRole = (role) => classified.find((c) => c.role === role);
  const preferred = byRole('problem') || byRole('submission') || byRole('contest') || byRole('solution');

  if (preferred) {
    return { canonicalUrl: preferred.canonical, slug: preferred.slug, site: preferred.host, urlSource: 'header', hasCanonicalUrl: true };
  }

  for (const id of ids) {
    const hit = slugIndex?.[String(id)];
    if (hit) {
      return {
        canonicalUrl: `https://leetcode.com/problems/${hit.slug}/description/`,
        slug: hit.slug, site: 'leetcode.com', urlSource: 'id-index', hasCanonicalUrl: true,
      };
    }
  }

  if (ids.length) {
    return {
      canonicalUrl: `https://leetcode.com/problemset/?search=${ids[0]}`,
      slug: null, site: 'leetcode.com', urlSource: 'search-fallback', hasCanonicalUrl: false,
    };
  }

  const article = byRole('article') || classified[0];
  if (article) {
    return { canonicalUrl: article.canonical, slug: article.slug, site: article.host, urlSource: 'header', hasCanonicalUrl: false };
  }

  return { canonicalUrl: null, slug: null, site: null, urlSource: 'none', hasCanonicalUrl: false };
}
