// Credential loading for the harvester scripts.
//
// Order: environment variable, then tools/data/secrets.json (gitignored).
// Everything here is optional. A missing credential is a skip with a clear
// message, never a build failure — generate.mjs must always be able to
// produce a complete site from the committed JSON alone.

import { readFileSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const HERE = dirname(fileURLToPath(import.meta.url));
export const DATA_DIR = join(HERE, '..', 'data');
export const ROOT = join(HERE, '..', '..');

function readSecretsFile() {
  try {
    return JSON.parse(readFileSync(join(DATA_DIR, 'secrets.json'), 'utf8'));
  } catch {
    return {};
  }
}

export function secrets() {
  const file = readSecretsFile();
  const clean = (v) => {
    const s = (v ?? '').toString().trim();
    return s === '' ? null : s;
  };
  return {
    youtubeApiKey: clean(process.env.YOUTUBE_API_KEY) ?? clean(file.youtubeApiKey),
    leetcodeUser: clean(process.env.LEETCODE_USER) ?? clean(file.leetcodeUser),
  };
}

/** Print the standard "you haven't set this up yet" note and exit cleanly. */
export function missing(what, howTo) {
  console.log(`\nskipped: no ${what}.`);
  console.log(howTo.split('\n').map((l) => '  ' + l).join('\n'));
  console.log('\nThe committed data is untouched and the site still builds.');
  process.exit(0);
}

export function readJson(name, fallback = null) {
  try {
    return JSON.parse(readFileSync(join(DATA_DIR, name), 'utf8'));
  } catch {
    return fallback;
  }
}

export const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

/** GET with a browser-ish UA; returns null rather than throwing. */
export async function get(url, { headers = {}, json = true } = {}) {
  try {
    const res = await fetch(url, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36',
        ...headers,
      },
    });
    if (!res.ok) return { ok: false, status: res.status, body: null };
    return { ok: true, status: res.status, body: json ? await res.json() : await res.text() };
  } catch (err) {
    return { ok: false, status: 0, body: null, error: err.message };
  }
}
