# Directory Layout

Standard layout for a Brabenetz-style Spring Boot CLI tool.

```
{{PROJECT_ROOT}}/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/{{PACKAGE_PATH}}/
│   │   │   ├── {{APP_CLASS}}.java
│   │   │   ├── {{RUNNER_CLASS}}.java
│   │   │   ├── {{BANNER_CLASS}}.java
│   │   │   ├── config/
│   │   │   │   ├── {{CONFIG_CLASS}}.java
│   │   │   │   └── WindowsRegistryConfigs.java   (optional)
│   │   │   ├── core/
│   │   │   │   ├── {{CORE_SERVICE}}.java
│   │   │   │   ├── GenerateWindowsRegistryEntries.java   (optional)
│   │   │   │   └── ImportWindowsRegistry.java            (optional)
│   │   │   ├── exception/
│   │   │   │   └── {{EXCEPTION_CLASS}}.java
│   │   │   └── utils/
│   │   └── assembly/
│   │       ├── config/
│   │       │   ├── application.properties
│   │       │   └── logback.xml
│   │       ├── shell/
│   │       │   ├── {{SHELL_SCRIPT_NAME}}.cmd
│   │       │   ├── {{SHELL_SCRIPT_NAME}}.sh
│   │       │   ├── generateWindowsRegistryEntries.cmd   (optional)
│   │       │   └── installWindowsRegistry.cmd           (optional)
│   │       ├── {{SHELL_SCRIPT_NAME}}-dist-win.xml
│   │       └── {{SHELL_SCRIPT_NAME}}-dist-unix.xml
│   └── test/
│       ├── java/{{PACKAGE_PATH}}/
│       │   ├── {{APP_CLASS}}IntegrationTest.java
│       │   └── {{BANNER_CLASS}}Test.java
│       └── resources/
│           ├── application.properties
│           └── logback-test.xml
└── target/   (generated)
    ├── {{ARTIFACT_ID}}-{{VERSION}}.jar
    ├── {{ARTIFACT_ID}}-{{VERSION}}-dist-win.zip
    └── {{ARTIFACT_ID}}-{{VERSION}}-dist-unix.tar.gz
```

## Conventions

- External config lives in `src/main/assembly/config/`, not on the classpath inside the JAR.
- Shell scripts reference `./config/logback.xml` and `./lib/{{ARTIFACT_ID}}.jar`.
- The runner uses `@Profile("!test")` so integration tests can call `main()` without double execution.
- Package `config` holds `@ConfigurationProperties` classes; `core` holds business logic and optional registry services.