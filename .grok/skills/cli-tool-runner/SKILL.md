---
name: cli-tool-runner
description: >
  Implement Spring Boot CLI entry point with CommandLineRunner, Apache Commons CLI,
  ConfigurationProperties, and custom exception handling. Use when creating a command-line
  application, CLI runner, or Spring Boot main class. Triggers: CLI-Tool, CommandLineRunner,
  Commons CLI, Spring Boot CLI, /cli-tool-runner.
---

# CLI Tool Runner

Create the application entry point and CLI argument handling.

## When to Use

- Standalone: add CLI runner to an existing Spring Boot project
- Called by `scaffold-cli-tool` after Maven setup

## Required Inputs

| Token | Example |
|---|---|
| `{{PACKAGE_NAME}}` | `net.brabenetz.tools.myapp` |
| `{{APP_CLASS}}` | `MyAppCommandLineApplication` |
| `{{RUNNER_CLASS}}` | `MyAppRunner` |
| `{{BANNER_CLASS}}` | `MyAppBanner` |
| `{{CONFIG_CLASS}}` | `MyAppConfigs` |
| `{{CONFIG_PREFIX}}` | `myapp` |
| `{{CORE_SERVICE}}` | `MyAppService` |
| `{{EXCEPTION_CLASS}}` | `MyAppException` |
| `{{SHELL_SCRIPT_NAME}}` | `myapp` |
| `{{APP_DISPLAY_NAME}}` | `My-App` |
| `{{AUTHOR}}` | `Brabenetz Harald` |

Bean naming: `{{CORE_SERVICE_BEAN}}` = camelCase of service (e.g. `myAppService`), `{{CONFIG_BEAN}}` = camelCase of config class.

## Steps

1. Generate from templates in `references/`:
   - `CommandLineApplication.java.template` → `{{APP_CLASS}}.java`
   - `CommandLineRunner.java.template` → `{{RUNNER_CLASS}}.java`
   - `ConfigProperties.java.template` → `config/{{CONFIG_CLASS}}.java`
   - `AppException.java.template` → `exception/{{EXCEPTION_CLASS}}.java`
   - `CoreService.java.template` → `core/{{CORE_SERVICE}}.java`
2. Generate config files:
   - `application.properties.template` → `src/main/assembly/config/application.properties`
   - `application-test.properties.template` → `src/test/resources/application.properties`
3. Generate `IntegrationTest.java.template` → test class (remove registry tests if registry not enabled).
4. Replace all `{{TOKEN}}` placeholders.

## Key Pattern: Banner Before Logging

```java
new SpringApplicationBuilder(AppClass.class)
    .bannerMode(Mode.OFF)
    .parent(new SpringApplicationBuilder(AppPrepare.class)
        .banner(new AppBanner())
        .run(args))
    .run(args);
```

The parent context prints the banner; the child context runs with `bannerMode.OFF` so logging does not interfere.

## CLI Conventions

- Apache Commons CLI with `DefaultParser` and `stopAtNonOption=true`
- Spring Boot externalized config: `--{{CONFIG_PREFIX}}.property=value`
- `@Profile("!test")` on runner
- Default (no args): print help via `HelpFormatter`
- `{{EXCEPTION_CLASS}}`: log message only (no stacktrace) unless DEBUG enabled

## Shell Launch Command

```cmd
java -Dlogging.config=./config/logback.xml -Duser.country=US -Duser.language=en -jar ./lib/{{ARTIFACT_ID}}.jar %*
```

## Registry Integration

If `cli-tool-registry` is enabled, extend the runner with `-gwre` and `-i` options. See `cli-tool-registry` skill for the runner extension pattern.