---
name: scaffold-cli-tool
description: >
  Scaffold a complete Java CLI tool project following the picture-copy schema: Spring Boot,
  custom banner, SLF4J/Logback, Maven Assembly (zip/tar.gz), and optional Windows Registry.
  Use when creating a new CLI tool, scaffolding a Brabenetz-style project, or rebuilding
  the picture-copy architecture. Triggers: neues CLI-Tool, scaffold, picture-copy Schema,
  Projekt aufbauen, /scaffold-cli-tool.
---

# Scaffold CLI Tool

Orchestrate creation of a complete CLI tool project by invoking sub-skills in order.

## When to Use

User wants a new Java command-line tool with:
- Spring Boot (no web)
- Custom ASCII banner
- External Logback config
- Zip + tar.gz distribution
- Optional Windows Registry context menu

## Step 1: Gather Project Data

Ask the user for these values (provide sensible defaults derived from artifact name):

| Field | Derivation Rule | Example |
|---|---|---|
| `groupId` | user input | `net.brabenetz.tools.myapp` |
| `artifactId` | user input (kebab-case) | `my-app` |
| `version` | default `1.0-SNAPSHOT` | `1.0-SNAPSHOT` |
| `package` | from groupId + artifact | `net.brabenetz.tools.myapp` |
| `appDisplayName` | artifactId → Title-Case | `My-App` |
| `appName` | artifactId → PascalCase | `MyApp` |
| `shellScriptName` | artifactId without hyphens | `myapp` |
| `author` | default `Brabenetz Harald` | |
| `TARGET_DIR` | new folder path (stored as `{{TARGET_DIR}}`) | `../my-app` |
| `enableRegistry` | yes/no | `yes` |
| `bannerLines` | ASCII art (8 lines) or patorjk link | |

Derived tokens (from `appName` = PascalCase of artifactId, `shellScriptName` = artifactId without hyphens):

```
{{PACKAGE_PATH}}  = package with dots → slashes
{{APP_CLASS}}     = appName + "CommandLineApplication"
{{RUNNER_CLASS}}  = appName + "Runner"
{{BANNER_CLASS}}  = appName + "Banner"
{{CONFIG_CLASS}}  = appName + "Configs"
{{CONFIG_PREFIX}} = shellScriptName
{{CORE_SERVICE}}  = appName + "Service"
{{EXCEPTION_CLASS}} = appName + "Exception"
{{CORE_SERVICE_BEAN}} = camelCase of {{CORE_SERVICE}}
{{CONFIG_BEAN}}   = camelCase of {{CONFIG_CLASS}}
```

## Step 2: Execute Sub-Skills in Order

| # | Skill | Action |
|---|---|---|
| 1 | `cli-tool-structure` | Create directory skeleton |
| 2 | `cli-tool-maven` | Generate `pom.xml` |
| 3 | `cli-tool-runner` | Application, Runner, Config, Exception, CoreService, tests |
| 4 | `cli-tool-banner` | Banner class + test; wire in Application |
| 5 | `cli-tool-logging` | logback.xml + logback-test.xml |
| 6 | `cli-tool-assembly` | Assembly descriptors + shell scripts |
| 7 | `cli-tool-registry` | Only if `enableRegistry=yes` |

For each sub-skill: read its `SKILL.md`, copy templates from `references/`, replace all `{{TOKEN}}` placeholders.

## Step 3: Build and Verify

```bash
cd {{TARGET_DIR}}
mvn verify -DmanifestVersionWithTimestamp=true
```

Expected artifacts:
- `target/{{ARTIFACT_ID}}-{{VERSION}}-dist-win.zip`
- `target/{{ARTIFACT_ID}}-{{VERSION}}-dist-unix.tar.gz`

## Step 4: Smoke Test

```bash
cd {{TARGET_DIR}}
mvn test
java -jar target/{{ARTIFACT_ID}}-{{VERSION}}.jar
```

Verify: banner prints, help text shows, no startup errors.

## Step 5: Checklist

Walk through [references/project-checklist.md](references/project-checklist.md) and confirm each item.

## Sub-Skill Locations

All sub-skills live in `.grok/skills/` of the picture-copy project:

```
cli-tool-structure/
cli-tool-maven/
cli-tool-runner/
cli-tool-banner/
cli-tool-logging/
cli-tool-assembly/
cli-tool-registry/
```

## Using Skills in a New Repo

These skills are project-scoped in picture-copy. To use in another repo, copy `.grok/skills/` to the target project or to `~/.grok/skills/` for global availability.

## What This Does NOT Include

- Business logic (replace `{{CORE_SERVICE}}.run()` stub with real implementation)
- Domain-specific dependencies (add to `pom.xml` after scaffolding)
- PictureCopy-specific features (EXIF, expression language, file copy algorithm)