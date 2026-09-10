/* Service worker: makes the deck work at zero bars.
 *
 * VERSION is rewritten by tools/generate.mjs on every build, so a redeploy
 * invalidates the old cache instead of serving a stale deck forever.
 *
 * Shell  -> cache first (it only changes when VERSION changes)
 * Data   -> stale-while-revalidate (instant offline, refreshes in background)
 */

const THUMB_CACHE_MAX = 240;   // ~1 thumbnail per problem, capped so it cannot grow forever

const VERSION = 'b3b1395-lqf1';
const CACHE = `dsa-deck-${VERSION}`;
// Thumbnails live outside the versioned cache: they are immutable per video id,
// so a redeploy has no reason to throw them away.
const THUMBS = 'dsa-deck-thumbs';

/** Keep a cache bounded by evicting the oldest entries. */
async function trimCache(cache, max) {
  const keys = await cache.keys();
  if (keys.length <= max) return;
  for (const k of keys.slice(0, keys.length - max)) await cache.delete(k);
}

const SHELL = [
  './',
  './index.html',
  './style.css',
  './app.js',
  './manifest.webmanifest',
  './icons/icon-192.png',
  './icons/icon-512.png',
];

self.addEventListener('install', (e) => {
  e.waitUntil((async () => {
    const cache = await caches.open(CACHE);
    // Cache the shell and the deck together: a shell without data is useless
    // offline, so precache data.json rather than waiting for a second visit.
    await cache.addAll([...SHELL, './data.json']).catch(async () => {
      // One bad entry must not fail the whole install.
      await Promise.all([...SHELL, './data.json'].map((u) => cache.add(u).catch(() => {})));
    });
    self.skipWaiting();
  })());
});

self.addEventListener('activate', (e) => {
  e.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys
      .filter((k) => k.startsWith('dsa-deck-') && k !== CACHE && k !== THUMBS)
      .map((k) => caches.delete(k)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', (e) => {
  const req = e.request;
  if (req.method !== 'GET') return;

  const url = new URL(req.url);

  // YouTube thumbnails: cache-first in their own bucket, so the video cards
  // still render offline (the player itself needs the network, and says so).
  // Responses are opaque cross-origin, which is fine to store and replay.
  if (url.hostname === 'i.ytimg.com') {
    e.respondWith((async () => {
      const cache = await caches.open(THUMBS);
      const hit = await cache.match(req);
      if (hit) return hit;
      try {
        const res = await fetch(req);
        if (res.status === 200 || res.type === 'opaque') {
          cache.put(req, res.clone());
          trimCache(cache, THUMB_CACHE_MAX);
        }
        return res;
      } catch {
        // A 1x1 transparent GIF keeps the layout intact when offline and
        // uncached; the poster's own gradient and title still read.
        return new Response(
          Uint8Array.from(atob('R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'), (c) => c.charCodeAt(0)),
          { headers: { 'content-type': 'image/gif' } },
        );
      }
    })());
    return;
  }

  if (url.origin !== location.origin) return;   // let fonts go to the network

  if (url.pathname.endsWith('/data.json')) {
    e.respondWith((async () => {
      const cache = await caches.open(CACHE);
      const hit = await cache.match(req);
      const fresh = fetch(req).then((res) => {
        if (res.ok) cache.put(req, res.clone());
        return res;
      }).catch(() => null);
      return hit ?? (await fresh) ?? new Response('{}', { headers: { 'content-type': 'application/json' } });
    })());
    return;
  }

  e.respondWith((async () => {
    const cache = await caches.open(CACHE);
    const hit = await cache.match(req, { ignoreSearch: true });
    if (hit) return hit;
    try {
      const res = await fetch(req);
      if (res.ok && res.type === 'basic') cache.put(req, res.clone());
      return res;
    } catch {
      // A navigation with nothing cached still gets the app shell.
      if (req.mode === 'navigate') {
        const shell = await cache.match('./index.html');
        if (shell) return shell;
      }
      return new Response('offline', { status: 503, statusText: 'offline' });
    }
  })());
});
