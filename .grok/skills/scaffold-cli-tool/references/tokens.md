# Placeholder Tokens

All templates use `{{TOKEN}}` placeholders. Replace every occurrence when scaffolding.

| Token | Source | Example (picture-copy) |
|---|---|---|
| `{{GROUP_ID}}` | Maven groupId | `net.brabenetz.tools.picture` |
| `{{ARTIFACT_ID}}` | Maven artifactId | `picture-copy` |
| `{{VERSION}}` | Maven version | `1.0-SNAPSHOT` |
| `{{PACKAGE_NAME}}` | Java package | `net.brabenetz.tools.picture.copy` |
| `{{PACKAGE_PATH}}` | package → path | `net/brabenetz/tools/picture/copy` |
| `{{APP_CLASS}}` | `{AppName}CommandLineApplication` | `PictureCopyCommandLineApplication` |
| `{{RUNNER_CLASS}}` | `{AppName}Runner` | `PictureCopyRunner` |
| `{{BANNER_CLASS}}` | `{AppName}Banner` | `PictureCopyBanner` |
| `{{CONFIG_CLASS}}` | `{AppName}Configs` | `PictureCopyConfigs` |
| `{{CONFIG_PREFIX}}` | shell script name | `picturecopy` |
| `{{CORE_SERVICE}}` | `{AppName}Service` | `PictureCopy` (domain-specific) |
| `{{CORE_SERVICE_BEAN}}` | camelCase service | `pictureCopy` |
| `{{CONFIG_BEAN}}` | camelCase config | `pictureCopyConfigs` |
| `{{EXCEPTION_CLASS}}` | `{AppName}Exception` | `PictureCopyException` |
| `{{APP_DISPLAY_NAME}}` | human-readable name | `Picture-Copy` |
| `{{APP_NAME}}` | PascalCase short name | `PictureCopy` |
| `{{SHELL_SCRIPT_NAME}}` | executable base name | `picturecopy` |
| `{{AUTHOR}}` | help footer | `Brabenetz Harald` |
| `{{BANNER_TEXT}}` | patorjk input (no brackets) | `Smart Naming` |
| `{{BANNER_TEXT_URL_ENCODED}}` | URL-encoded banner text | `Smart%20Naming` |
| `{{BANNER_LINE_1}}` … `{{BANNER_LINE_6}}` | patorjk Big art lines | from patorjk.com, width ≥ 200 |
| `{{BANNER_LINE_7}}` | underscore underline with spared descender | `___… __/ | ___…` |
| `{{BANNER_LINE_8}}` | dash underline with spared descender | `---… |___/ ---…` |
| `{{REGISTRY_ENTRIES}}` | `addCommandToRegistry()` calls | see registry-entries.example.txt |
| `{{PROJECT_ROOT}}` | illustrative root in directory layout | project root path |
| `{{TARGET_DIR}}` | scaffold output directory | `../my-app` |

Shell scripts in assembly use `${project.artifactId}` (Maven filtering), not `{{ARTIFACT_ID}}`.

## Derivation Rules (Orchestrator)

```
appName       = artifactId → PascalCase (my-app → MyApp)
shellScriptName = artifactId without hyphens (my-app → myapp)
configPrefix  = shellScriptName
package       = groupId + "." + last segment OR user-specified
```