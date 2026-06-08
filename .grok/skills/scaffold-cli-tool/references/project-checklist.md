# Project Scaffold Checklist

After scaffolding a new CLI tool, verify each item:

## Structure
- [ ] `pom.xml` with correct `groupId`, `artifactId`, `version`
- [ ] Package structure: `config/`, `core/`, `exception/`, `utils/`
- [ ] `src/main/assembly/config/` with `application.properties` and `logback.xml`
- [ ] `src/main/assembly/shell/` with `.cmd` and `.sh` scripts
- [ ] Assembly descriptors for win (zip) and unix (tar.gz)

## Build
- [ ] `mvn verify` succeeds without errors
- [ ] `target/{{ARTIFACT_ID}}-{{VERSION}}-dist-win.zip` exists
- [ ] `target/{{ARTIFACT_ID}}-{{VERSION}}-dist-unix.tar.gz` exists
- [ ] Zip contains: `{{SHELL_SCRIPT_NAME}}.cmd`, `config/`, `lib/{{ARTIFACT_ID}}.jar`

## Runtime
- [ ] Running without args shows ASCII banner + help text
- [ ] `-run` executes main logic and logs to console/file
- [ ] Version appears in banner (requires `manifestVersionWithTimestamp` profile for timestamp)

## Logging
- [ ] Console shows WARN+ with short pattern
- [ ] `./log/app.log` receives all log output
- [ ] Application startup log goes to file only (not console)

## Registry (if enabled)
- [ ] `-gwre` generates `{{APP_NAME}}-Registry.cmd` and `{{APP_NAME}}-Install.reg`
- [ ] `-i` imports registry (requires Administrator)
- [ ] Context menu entries appear in File Explorer

## Tests
- [ ] `mvn test` passes
- [ ] Banner test prints without exception
- [ ] Integration tests cover help, run, and registry generation