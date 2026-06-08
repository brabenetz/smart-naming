---
name: cli-tool-structure
description: >
  Create the standard Maven directory layout for a Brabenetz-style Spring Boot CLI tool.
  Use when scaffolding project structure, setting up src/main/assembly, or organizing
  config/core/exception/utils packages. Triggers: Projektstruktur, directory layout,
  CLI-Tool Struktur, /cli-tool-structure.
---

# CLI Tool Structure

Create the directory layout for a new Java CLI tool following the picture-copy schema.

## When to Use

- Standalone: create only the folder skeleton in an existing or new project
- Called by `scaffold-cli-tool` as the first step

## Required Inputs

Collect or derive these values before creating directories:

| Token | Example |
|---|---|
| `{{PACKAGE_PATH}}` | `net/brabenetz/tools/myapp` |
| `{{APP_CLASS}}` | `MyAppCommandLineApplication` |
| `{{SHELL_SCRIPT_NAME}}` | `myapp` |

## Steps

1. Read [references/directory-layout.md](references/directory-layout.md) for the full tree.
2. Create all directories under `src/main/java/{{PACKAGE_PATH}}/`:
   - `config/`, `core/`, `exception/`, `utils/`
3. Create assembly directories:
   - `src/main/assembly/config/`
   - `src/main/assembly/shell/`
4. Create test directories:
   - `src/test/java/{{PACKAGE_PATH}}/`
   - `src/test/resources/`
5. Do **not** place `application.properties` or `logback.xml` on the classpath inside `src/main/resources/`. External config lives in `src/main/assembly/config/`.

## Conventions

- Shell scripts use `./config/logback.xml` and `./lib/{{ARTIFACT_ID}}.jar`
- Runner class gets `@Profile("!test")` (added by `cli-tool-runner`)
- Registry classes in `core/` are optional (added by `cli-tool-registry`)