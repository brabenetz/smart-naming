---
name: cli-tool-assembly
description: >
  Configure Maven Assembly Plugin to build zip (Windows) and tar.gz (Unix) distributions
  with shell scripts, external config, and fat JAR. Use when packaging CLI tools,
  creating distributions, zip, tar.gz, or assembly descriptors. Triggers: Assembly,
  Packaging, zip, tar.gz, Distribution, /cli-tool-assembly.
---

# CLI Tool Assembly

Package the CLI tool as platform-specific distributions.

## When to Use

- Standalone: add assembly packaging to an existing Maven project
- Called by `scaffold-cli-tool` after logging setup

## Required Inputs

| Token | Example |
|---|---|
| `{{GROUP_ID}}` | `net.brabenetz.tools.myapp` |
| `{{ARTIFACT_ID}}` | `my-app` |
| `{{SHELL_SCRIPT_NAME}}` | `myapp` |

## Steps

1. Copy assembly descriptors:
   - `references/dist-win.xml.template` → `src/main/assembly/{{SHELL_SCRIPT_NAME}}-dist-win.xml`
   - `references/dist-unix.xml.template` → `src/main/assembly/{{SHELL_SCRIPT_NAME}}-dist-unix.xml`
2. Copy shell scripts:
   - `references/app.cmd.template` → `src/main/assembly/shell/{{SHELL_SCRIPT_NAME}}.cmd`
   - `references/app.sh.template` → `src/main/assembly/shell/{{SHELL_SCRIPT_NAME}}.sh`
3. Replace `{{TOKEN}}` placeholders in descriptors (not in `.cmd`/`.sh` — those use `${project.artifactId}` for Maven filtering).
4. Ensure `pom.xml` references both descriptors (see `cli-tool-maven`).
5. Run `mvn verify` and check artifacts.

## Output Artifacts

```
target/{{ARTIFACT_ID}}-{{VERSION}}-dist-win.zip
target/{{ARTIFACT_ID}}-{{VERSION}}-dist-unix.tar.gz
```

## Distribution Layout

```
{{SHELL_SCRIPT_NAME}}.cmd  (or .sh)
config/
  application.properties
  logback.xml
lib/
  {{ARTIFACT_ID}}.jar
```

## Platform Differences

| Aspect | Windows (zip) | Unix (tar.gz) |
|---|---|---|
| Scripts | `*.cmd`, `*.bat` | `*.sh` |
| Line endings | DOS | Unix |
| Script permissions | default | `0744` |
| JAR name | without version | without version |

## Critical Rules

- `includeBaseDirectory` = `false` (flat root)
- Assembly phase = `verify` (after Spring Boot repackage)
- `outputFileNameMapping` strips version: `${artifact.artifactId}.${artifact.extension}`
- Shell scripts use `${project.artifactId}` — replaced by Maven resource filtering during assembly

## Registry Scripts

If registry is enabled, also add `generateWindowsRegistryEntries.cmd` and `installWindowsRegistry.cmd` from `cli-tool-registry`.