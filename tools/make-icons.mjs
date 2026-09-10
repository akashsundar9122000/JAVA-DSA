#!/usr/bin/env node
// Generate the PWA icons as real PNGs.
//
// iOS needs a PNG apple-touch-icon to install to the home screen, and an
// SVG-only manifest will not do it. Rather than add an image dependency, this
// writes PNGs directly: node has zlib, and PNG is a short container format.
//
//   node tools/make-icons.mjs
//
// Design: the accent `</>` glyph on the app's own dark ground, drawn with
// signed-distance line math so the strokes are anti-aliased. Maskable, so the
// glyph stays inside the 80% safe zone Android may crop to a circle.

import { deflateSync } from 'node:zlib';
import { writeFileSync, mkdirSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const OUT = join(dirname(fileURLToPath(import.meta.url)), '..', 'docs', 'icons');

const BG = [0x12, 0x16, 0x1c];
const FG = [0x5c, 0xcf, 0xc0];

/** Shortest distance from point p to segment ab. */
function distToSegment(px, py, ax, ay, bx, by) {
  const dx = bx - ax;
  const dy = by - ay;
  const len2 = dx * dx + dy * dy;
  let t = len2 === 0 ? 0 : ((px - ax) * dx + (py - ay) * dy) / len2;
  t = Math.max(0, Math.min(1, t));
  const cx = ax + t * dx;
  const cy = ay + t * dy;
  return Math.hypot(px - cx, py - cy);
}

function crc32(buf) {
  let c = ~0;
  for (let i = 0; i < buf.length; i++) {
    c ^= buf[i];
    for (let k = 0; k < 8; k++) c = (c >>> 1) ^ (0xEDB88320 & -(c & 1));
  }
  return ~c >>> 0;
}

function chunk(type, data) {
  const len = Buffer.alloc(4);
  len.writeUInt32BE(data.length);
  const body = Buffer.concat([Buffer.from(type, 'latin1'), data]);
  const crc = Buffer.alloc(4);
  crc.writeUInt32BE(crc32(body));
  return Buffer.concat([len, body, crc]);
}

function png(size, pixels) {
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(size, 0);
  ihdr.writeUInt32BE(size, 4);
  ihdr[8] = 8;   // bit depth
  ihdr[9] = 2;   // colour type: truecolour RGB
  return Buffer.concat([
    Buffer.from([0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A]),
    chunk('IHDR', ihdr),
    chunk('IDAT', deflateSync(pixels, { level: 9 })),
    chunk('IEND', Buffer.alloc(0)),
  ]);
}

function draw(size) {
  // Raw scanlines: one filter byte (0 = none) then RGB triples.
  const raw = Buffer.alloc(size * (1 + size * 3));
  const u = size / 100;               // one "design unit" = 1% of the icon
  const stroke = 7 * u;               // glyph stroke half-width basis
  const ss = 2;                       // supersampling factor per axis

  // `</>` inside the maskable safe zone: chevrons at 26/74 and a slash.
  const segs = [
    [[36, 32], [22, 50]], [[22, 50], [36, 68]],   // left chevron
    [[64, 32], [78, 50]], [[78, 50], [64, 68]],   // right chevron
    [[56, 28], [44, 72]],                          // slash
  ];

  for (let y = 0; y < size; y++) {
    const rowStart = y * (1 + size * 3);
    raw[rowStart] = 0;
    for (let x = 0; x < size; x++) {
      let cov = 0;
      for (let sy = 0; sy < ss; sy++) {
        for (let sx = 0; sx < ss; sx++) {
          const px = x + (sx + 0.5) / ss;
          const py = y + (sy + 0.5) / ss;
          let d = Infinity;
          for (const [[ax, ay], [bx, by]] of segs) {
            d = Math.min(d, distToSegment(px, py, ax * u, ay * u, bx * u, by * u));
          }
          // half-stroke with a one-pixel soft edge
          const edge = stroke / 2;
          if (d <= edge) cov += 1;
          else if (d <= edge + u) cov += 1 - (d - edge) / u;
        }
      }
      cov /= ss * ss;

      const o = rowStart + 1 + x * 3;
      for (let c = 0; c < 3; c++) {
        raw[o + c] = Math.round(BG[c] + (FG[c] - BG[c]) * cov);
      }
    }
  }
  return raw;
}

mkdirSync(OUT, { recursive: true });
for (const size of [192, 512]) {
  const file = join(OUT, `icon-${size}.png`);
  writeFileSync(file, png(size, draw(size)));
  console.log(`wrote docs/icons/icon-${size}.png`);
}
