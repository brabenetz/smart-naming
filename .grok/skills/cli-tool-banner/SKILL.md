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
| `{{BANNER_LINE_1}}` … `{{BANNER_LINE_8}}` | ASCII art lines |

## Steps

1. Generate ASCII art at http://patorjk.com/software/taag/ (recommended font: Big).
2. Copy [references/Banner.java.template](references/Banner.java.template), replace tokens.
3. Set `BANNER` array lines from generated art. Adjust array size if needed.
4. Copy [references/BannerTest.java.template](references/BannerTest.java.template) for unit test.
5. Wire banner in `{{APP_CLASS}}` via parent `SpringApplicationBuilder` (see `cli-tool-runner`).

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