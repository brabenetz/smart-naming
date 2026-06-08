---
name: cli-tool-logging
description: >
  Configure SLF4J and Logback for a Spring Boot CLI tool with external logback.xml,
  console and file appenders, and test-specific logging. Use when setting up logging,
  logback.xml, SLF4J, or log configuration. Triggers: Logging, Logback, SLF4J,
  logback.xml, /cli-tool-logging.
---

# CLI Tool Logging

Set up SLF4J + Logback with external configuration and Spring Boot includes.

## When to Use

- Standalone: add or fix logging in a CLI tool
- Called by `scaffold-cli-tool` after banner setup

## Required Inputs

| Token | Example |
|---|---|
| `{{PACKAGE_NAME}}` | `net.brabenetz.tools.myapp` |
| `{{APP_CLASS}}` | `MyAppCommandLineApplication` |

## Steps

1. Copy templates to target locations:
   - `references/logback.xml.template` → `src/main/assembly/config/logback.xml`
   - `references/logback-test.xml.template` → `src/test/resources/logback-test.xml`
2. Replace `{{TOKEN}}` placeholders.
3. Ensure shell scripts pass `-Dlogging.config=./config/logback.xml`.

## SLF4J Usage in Java

```java
private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);
```

Provided by `spring-boot-starter` (logback-classic on classpath).

## Production Logback Hierarchy

| Logger | Level | Appenders | Purpose |
|---|---|---|---|
| `{{PACKAGE_NAME}}.{{APP_CLASS}}` | default | FILE only | Startup info to file, not console |
| `net.brabenetz` | INFO | inherited | Application packages |
| root | WARN | CONSOLE + FILE | Everything else |

## Spring Boot Includes

```xml
<include resource="org/springframework/boot/logging/logback/defaults.xml"/>
<include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
<include resource="org/springframework/boot/logging/logback/file-appender.xml"/>
```

## Log File

- Path: `./log/app.log` (relative to working directory)
- Console pattern: short, ANSI colors
- File pattern: full timestamp, thread, logger name

## Test Configuration

- Console only, no file appender
- `net.brabenetz` at DEBUG for troubleshooting tests
- Placed in `src/test/resources/logback-test.xml` (auto-picked by Spring Boot Test)

## Why External Config

`logback.xml` lives in `src/main/assembly/config/`, **not** inside the JAR. End users can edit logging without rebuilding.