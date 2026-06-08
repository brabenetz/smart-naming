#!/usr/bin/env node
/**
 * Build underline lines 7-8 for a patorjk/figlet Big-font banner.
 * Preserves descender glyphs (g, y, p, q, j) at their original column positions.
 *
 * Usage:
 *   node compute-banner-underlines.js <art-line-7> <art-line-8> [width]
 *
 * Example:
 *   node compute-banner-underlines.js \
 *     "                                                                           __/ |        " \
 *     "                                                                          |___/         "
 */

const ORIG_LINE7 = process.argv[2];
const ORIG_LINE8 = process.argv[3];
const TARGET_WIDTH = parseInt(process.argv[4] || '88', 10);

if (!ORIG_LINE7 || !ORIG_LINE8) {
  console.error('Usage: node compute-banner-underlines.js <art-line-7> <art-line-8> [width]');
  process.exit(1);
}

function trimTrailingSpaces(line) {
  return line.replace(/\s+$/, '');
}

function findDescenderSegment(line) {
  const trimmed = trimTrailingSpaces(line);
  const start = trimmed.search(/\S/);
  if (start === -1) {
    throw new Error(`No descender content in: ${JSON.stringify(line)}`);
  }
  return { start, text: trimmed.slice(start) };
}

function buildUnderlineLine(width, fillChar, mid, midStartCol) {
  const prefixFillLen = midStartCol - 1;
  const suffixFillLen = width - midStartCol - mid.length;
  if (prefixFillLen < 0 || suffixFillLen < 0) {
    throw new Error(`Cannot fit descender "${mid}" at column ${midStartCol} in width ${width}`);
  }
  return ' ' + fillChar.repeat(prefixFillLen) + mid + fillChar.repeat(suffixFillLen);
}

const desc7 = findDescenderSegment(ORIG_LINE7);
const desc8 = findDescenderSegment(ORIG_LINE8);

const LINE7 = buildUnderlineLine(TARGET_WIDTH, '_', desc7.text, desc7.start);
const LINE8 = buildUnderlineLine(TARGET_WIDTH, '-', desc8.text, desc8.start);

[LINE7, LINE8].forEach((line, i) => {
  if (line.length !== TARGET_WIDTH) {
    throw new Error(`Line ${i + 7} length ${line.length} != ${TARGET_WIDTH}`);
  }
});

console.log('Line 7 (underscores):');
console.log(LINE7);
console.log('Line 8 (dashes):');
console.log(LINE8);
console.log('\nJava literals:');
console.log(`"${LINE7.replace(/\\/g, '\\\\')}",`);
console.log(`"${LINE8.replace(/\\/g, '\\\\')}",`);