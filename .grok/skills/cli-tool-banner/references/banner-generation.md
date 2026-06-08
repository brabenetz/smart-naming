# Banner Generation (patorjk Big + Underline)

## 1. Generate ASCII Art

1. Open http://patorjk.com/software/taag/
2. Font: **Big**
3. Text: `<< {{BANNER_TEXT}} >>` (display name with hyphens replaced by spaces)
   - Example: `Smart-Naming` → `<< Smart Naming >>`
4. Set width ≥ 200 on patorjk (or use figlet `-w 200`) so `>>` stays on the same line block.

Direct link pattern:

```
http://patorjk.com/software/taag/#p=display&f=Big&t=%3C%3C%20{BANNER_TEXT_URL_ENCODED}%20%3E%3E&w=200
```

Alternative CLI (matches patorjk Big font):

```bash
npx figlet-cli -f Big -w 200 "<< Smart Naming >>"
```

## 2. Big Variant with Underline

Use the first **6 lines** of the generated art as-is (pad trailing spaces to uniform width).

Replace figlet lines 7–8 (descender rows for letters like `g`, `y`, `p`) with decorative underlines:

| Line | Fill | Rule |
|------|------|------|
| 7 | `_` | Leading space + underscores, then descender text from art line 7, then underscores |
| 8 | `-` | Leading space + dashes, then descender text from art line 8, then dashes |

The descender glyphs (`__/ |`, `|___/`, `| | __  __/ |`, etc.) must remain **visible and uncrossed**.

## 3. Compute Underlines

Use [compute-banner-underlines.js](compute-banner-underlines.js):

```bash
node .grok/skills/cli-tool-banner/references/compute-banner-underlines.js \
  "                                                                           __/ |        " \
  "                                                                          |___/         "
```

## 4. Final Banner Array

- 8 lines total (6 art + 2 underline)
- All lines same width (typically 88 for Big font with `<< ... >>` brackets)
- `STRAP_LINE_SIZE` = max line length
- Pad art lines 1–6 with trailing spaces if figlet output is shorter