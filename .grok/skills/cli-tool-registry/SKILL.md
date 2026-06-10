---
name: cli-tool-registry
description: >
  Add Windows Registry integration for File Explorer context menu entries: generate .reg
  files, wrapper .cmd, and install via reg import. Use when adding Windows Registry,
  context menu, Explorer integration, or registry install. Triggers: Windows Registry,
  Kontextmenü, reg import, Explorer, /cli-tool-registry.
---

# CLI Tool Registry

Add Windows File Explorer context menu integration.

## When to Use

- Standalone: add registry support to an existing CLI tool
- Called by `scaffold-cli-tool` when user opts in to registry features

## Required Inputs

| Token | Example |
|---|---|
| `{{PACKAGE_NAME}}` | `net.brabenetz.tools.myapp` |
| `{{APP_NAME}}` | `MyApp` (PascalCase, for file names) |
| `{{SHELL_SCRIPT_NAME}}` | `myapp` |
| `{{APP_DISPLAY_NAME}}` | `My-App` |
| `{{EXCEPTION_CLASS}}` | `MyAppException` |
| `{{ARTIFACT_ID}}` | `my-app` |
| `{{CONFIG_PREFIX}}` | `myapp` |
| `{{RUNNER_CLASS}}` | `MyAppRunner` |

Plus per context menu entry: command name and CLI arguments.

## Steps

1. Generate Java classes from templates:
   - `WindowsRegistryConfigs.java.template` → `config/WindowsRegistryConfigs.java`
   - `GenerateWindowsRegistryEntries.java.template` → `core/GenerateWindowsRegistryEntries.java`
   - `ImportWindowsRegistry.java.template` → `core/ImportWindowsRegistry.java`
2. Replace `{{REGISTRY_ENTRIES}}` in `GenerateWindowsRegistryEntries` with `addCommandToRegistry()` calls.

**Multi-file selection (recommended for file tools):**

```java
addCommandToRegistry(lines, "Run {{APP_DISPLAY_NAME}}", commandFile,
        "-run", "--files", "%*");
```

Registry key: `HKEY_CLASSES_ROOT\*\shell\` (all file types, multi-select).

`addCommandToRegistry()` sets `"MultiSelectModel"="Player"` on the shell key. **Required** when using `%*` — without it Windows defaults to Document mode (one process per file, `%*` empty → CLI errors like missing `--files`).

**Single folder selection (legacy):**

```java
addCommandToRegistry(lines, "Run {{APP_DISPLAY_NAME}}", commandFile,
        "-copy", "--{{CONFIG_PREFIX}}.source-folder=%1");
```

Registry key: `HKEY_CLASSES_ROOT\Directory\shell\`.

3. Add shell scripts:
   - `generateRegistry.cmd.template` → `src/main/assembly/shell/generateWindowsRegistryEntries.cmd`
   - `installRegistry.cmd.template` → `src/main/assembly/shell/installWindowsRegistry.cmd`
4. Extend `{{RUNNER_CLASS}}` with registry CLI options using [references/CommandLineRunner-registry.java.template](references/CommandLineRunner-registry.java.template).
5. Add `--files` option to runner (required with `--run`) — see `cli-tool-runner`.
6. Add registry properties to `application.properties` (see `cli-tool-runner` template).

## Runner Extension

Add to the runner's `Options` (full fragment in `CommandLineRunner-registry.java.template`):

```java
Option generateRegistryOption = new Option("gwre", "generateWindowsRegistryEntries", false,
        "generate Windows Registry files for File Explorer context menu");
Option importRegistryOption = new Option("i", "install", false,
        "install Windows Registry entries (requires Administrator)");

// In parse handler:
} else if (cmd.hasOption("gwre")) {
    generateWindowsRegistryEntries.generateRegistry();
} else if (cmd.hasOption("i")) {
    File registryFile = generateWindowsRegistryEntries.generateRegistry();
    importWindowsRegistry.importRegistry(registryFile);
}
```

Inject `GenerateWindowsRegistryEntries` and `ImportWindowsRegistry` via `@Resource`.

## `%*` Quoting Rule

When the last argument is `%*`, it must remain **unquoted** in the registry command line so Explorer expands all selected file paths:

```
"\"...\MyApp-Registry.cmd\" \"-run\" \"--files\" %*"
```

`buildRegistryCommandLine()` in the template handles this automatically.

## Generated Files

| File | Purpose |
|---|---|
| `{{APP_NAME}}-Registry.cmd` | Wrapper: `cd` to install dir, call `{{SHELL_SCRIPT_NAME}}.cmd` |
| `{{APP_NAME}}-Install.reg` | Registry entries under `HKEY_CLASSES_ROOT\*\shell\` or `Directory\shell\` |

Encoding: ISO-8859-1 for Windows compatibility.

## Configuration Properties

```properties
windows-registry.target-folder=./windows-registry
windows-registry.installation-folder=./
```

## Installation

- Manual: double-click `{{APP_NAME}}-Install.reg` or run `installWindowsRegistry.cmd` as Administrator
- CLI: `{{SHELL_SCRIPT_NAME}}.cmd -i` (also requires Administrator)
- `reg import` success message appears on stderr (handled in `ImportWindowsRegistry`)

## Error Handling

- Missing admin rights: `"You must run this script as Administrator"`
- Missing `{{SHELL_SCRIPT_NAME}}.cmd` in installation folder: validation error at generation time