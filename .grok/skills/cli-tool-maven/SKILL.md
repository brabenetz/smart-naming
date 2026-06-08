---
name: cli-tool-maven
description: >
  Generate pom.xml with Spring Boot, Commons CLI, Assembly Plugin, and JAR manifest
  configuration for a Brabenetz-style CLI tool. Use when setting up Maven dependencies,
  maven-assembly-plugin, spring-boot-maven-plugin, or build packaging. Triggers: Maven,
  pom.xml, Dependencies, Assembly Plugin, /cli-tool-maven.
---

# CLI Tool Maven

Generate `pom.xml` from the picture-copy schema.

## When to Use

- Standalone: create or update `pom.xml` in a CLI tool project
- Called by `scaffold-cli-tool` after structure creation

## Required Inputs

| Token | Example |
|---|---|
| `{{GROUP_ID}}` | `net.brabenetz.tools.myapp` |
| `{{ARTIFACT_ID}}` | `my-app` |
| `{{VERSION}}` | `1.0-SNAPSHOT` |
| `{{SHELL_SCRIPT_NAME}}` | `myapp` |

## Steps

1. Copy [references/pom-template.xml](references/pom-template.xml) to project root as `pom.xml`.
2. Replace all `{{TOKEN}}` placeholders.
3. Verify assembly descriptor paths match: `src/main/assembly/{{SHELL_SCRIPT_NAME}}-dist-win.xml` and `-dist-unix.xml`.
4. Add project-specific dependencies **below** the baseline commons/spring-boot block.

## Baseline Stack

- **Parent:** Spring Boot `2.1.8.RELEASE`, Java `1.8`
- **Core deps:** `spring-boot-starter`, `commons-cli`, `commons-io`, `commons-lang3`, `commons-text`, `commons-collections4`
- **Test deps:** `spring-boot-starter-test`, `junit`, `assertj-core`
- **Plugins:** `spring-boot-maven-plugin` (package), `maven-assembly-plugin` (verify), `maven-jar-plugin`, `sortpom-maven-plugin`

## Critical: Assembly Phase

Assembly must run in phase `verify`, **not** `package`. Spring Boot repackages the JAR during `package`; assembly needs the final fat JAR.

## Optional: Timestamped Version in Banner

Build with profile for manifest version:

```bash
mvn verify -DmanifestVersionWithTimestamp=true
```

This sets `Implementation-Version` to `${project.version}-${maven.build.timestamp}`, displayed by the custom banner.

## Upgrade Note

Baseline is Spring Boot 2.1.8 / Java 8. For newer projects, update parent version and `java.version` together; verify Logback includes still resolve.