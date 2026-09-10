/* Service worker: makes the deck work at zero bars.
 *
 * VERSION is rewritten by tools/generate.mjs on every build, so a redeploy
 * invalidates the old cache instead of serving a stale deck forever.
 *
 * Shell  -> cache first (it only changes when VERSION changes)
 * Data   -> stale-while-revalidate (instant offline, refreshes in background)
 */

const VERSION = 'ca37025-j3r6';
const CACHE = `dsa-deck-${VERSION}`;

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
    await Promise.all(keys.filter((k) => k.startsWith('dsa-deck-') && k !== CACHE).map((k) => caches.delete(k)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', (e) => {
  const req = e.request;
  if (req.method !== 'GET') return;

  const url = new URL(req.url);
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
