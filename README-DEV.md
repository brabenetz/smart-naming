# Smart-Naming — Developer Guide

Compact reference for building, testing, and releasing Smart-Naming locally.

See also: [README.md](README.md) (end-user documentation).

## Prerequisites

- **Java 8+** (project compiles with `java.version=1.8` in `pom.xml`)
- **Maven 3.x**

## Build

```bash
mvn clean install
```

Full distribution packages (Windows zip + Unix tar.gz):

```bash
mvn clean verify
```

Artifacts in `target/`:

- `smart-naming-*-SNAPSHOT.jar`
- `smart-naming-*-SNAPSHOT-smartnaming-dist-win.zip`
- `smart-naming-*-SNAPSHOT-smartnaming-dist-unix.tar.gz`

Optional manifest timestamp:

```bash
mvn clean verify -DmanifestVersionWithTimestamp=true
```

## Test

**From the IDE:** run JUnit test classes under `src/test/java`.

**From the command line:**

```bash
mvn test
```

Integration tests use WireMock on port `18089` and profile `test`.

## Manual run and local LLM testing

The easiest way to test against a real API (LM Studio, Grok, etc.):

1. Set the IDE working directory to the **project root**.
2. Run [`SmartNamingManuellTest.java`](src/test/java/net/brabenetz/tools/smart/naming/SmartNamingManuellTest.java) (`main()` method).
3. Uses Spring profile `manual` and [`src/test/resources/application-manual.yml`](src/test/resources/application-manual.yml).
4. Example files: `src/test/data/example-files/`.

### API tokens for manual runs

Create `application.properties` in the **project root** (listed in `.gitignore`):

```properties
smartnaming.models.grok.auth.api-token=xai-your-key-here
smartnaming.models.local-qwen.auth.api-token=lm-studio
```

Replace model keys as needed. Tokens are encrypted on startup via [secured-properties](https://github.com/brabenetz/secured-properties).

Adjust `smartnaming.used-model` in `application-manual.yml` or uncomment different `getFiles(...)` lines in the test class.

## Release

Releases are created via GitHub Actions:

1. Open **Actions** → **Create Release** → **Run workflow**.
2. Provide release version, release notes, and next SNAPSHOT version.
3. The workflow builds with `mvn verify`, creates a GitHub Release, and uploads JAR + distribution archives.

See [`.github/workflows/release.yml`](.github/workflows/release.yml).

## Project layout

| Path | Purpose |
|------|---------|
| `src/main/java/` | Application code |
| `src/main/assembly/` | Distribution descriptors, shell scripts, external config |
| `src/main/resources/application.yml` | Default prompt and validation pattern (bundled in JAR) |
| `src/test/` | Unit and integration tests |
| `.grok/skills/` | Agent skills for scaffolding and conventions |