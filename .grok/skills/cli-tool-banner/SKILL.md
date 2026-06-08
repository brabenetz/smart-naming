---
name: cli-tool-banner
description: >
  Create a custom Spring Boot ASCII-art banner with version display and colored output.
  Use when adding a startup banner, ASCII art logo, or AppPrepare system info logging.
  Triggers: Banner, ASCII art, Spring Boot Banner, patorjk, /cli-tool-banner.
---

# CLI Tool Banner

Implement a custom `org.springframework.boot.Banner` with ASCII art and version line.

## When to Use

- Standalone: add or update banner in an existing CLI tool
- Called by `scaffold-cli-tool` after runner creation

## Required Inputs

| Token | Example |
|---|---|
| `{{BANNER_CLASS}}` | `MyAppBanner` |
| `{{APP_CLASS}}` | `MyAppCommandLineApplication` |
| `{{APP_DISPLAY_NAME}}` | `My-App` |
| `{{BANNER_TEXT}}` | `My App` (display name, hyphens → spaces) |
| `{{BANNER_LINE_1}}` … `{{BANNER_LINE_8}}` | 6 art lines + 2 underline lines |

## Steps

1. Generate ASCII art at http://patorjk.com/software/taag/:
   - Font: **Big**
   - Text: `<< {{BANNER_TEXT}} >>` (e.g. `<< Smart Naming >>`)
   - Width ≥ 200 (prevents `>>` from wrapping below)
   - See [references/banner-generation.md](references/banner-generation.md)
2. Apply the **Big Variant** underline pattern (see below).
3. Copy [references/Banner.java.template](references/Banner.java.template), replace tokens.
4. Set `BANNER` array: 6 art lines + 2 underline lines. Pad to uniform width.
5. Copy [references/BannerTest.java.template](references/BannerTest.java.template) for unit test.
6. Wire banner in `{{APP_CLASS}}` via parent `SpringApplicationBuilder` (see `cli-tool-runner`).

## Big Variant Underline

Figlet/patorjk Big font renders descenders (`g`, `y`, `p`, `q`, `j`) on lines 7–8. Replace those rows:

| Line | Fill | Content |
|------|------|---------|
| 7 | `_` | Leading space + underscores + **preserved descender from art line 7** + underscores |
| 8 | `-` | Leading space + dashes + **preserved descender from art line 8** + dashes |

The descender glyphs must not be overwritten (e.g. `__/ |` and `|___/` for `g`).

Compute underline lines with [references/compute-banner-underlines.js](references/compute-banner-underlines.js).

Reference implementations: `PictureCopyBanner.java`, `MailBackupBanner.java`.

## Version Display

Version comes from JAR manifest `Implementation-Version`:

```java
String version = AppClass.class.getPackage().getImplementationVersion();
```

Requires `maven-jar-plugin` with `addDefaultImplementationEntries=true`. Use `-DmanifestVersionWithTimestamp=true` for build timestamp suffix.

## AppPrepare Hook

In `{{APP_CLASS}}`, inner class `AppPrepare` implements `InitializingBean` and logs:

```
Current Java-Version: ...; OS: ...; Timezone: ...; Lang: ...
```

This runs after the banner, through normal SLF4J logging.

## Design Rules

- `STRAP_LINE_SIZE` = max line length in `BANNER` array (for right-aligned version)
- App name line: green ANSI color, faint version
- Avoid Unicode box-drawing fonts (broken in Git Bash on Windows)
- All 8 `BANNER` lines must have identical width (pad with trailing spaces)