---
name: cli-tool-registry
description: >
  Add Windows Registry integration for File Explorer context menu entries: generate .reg
  files, wrapper .cmd, and install via reg import. Supports directory vs. file targets,
  per-file vs. batched file invocation, and context-menu-launcher (singleinstance.exe).
  Triggers: Windows Registry, Kontextmenü, reg import, Explorer, context-menu-launcher,
  singleinstance, MultiSelectModel, Mehrfachauswahl, Verzeichnis-Kontextmenü, /cli-tool-registry.
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

Plus per context menu entry: command name, target type (directory or files), and processing mode.

## Windows Explorer Behavior (read first)

Legacy registry shell verbs behave differently than many developers expect:

- When the user selects **multiple files**, Explorer often launches the command **once per file** — not once with all paths.
- `MultiSelectModel` controls **visibility and selection limits** (`Document` ≈ 15 items, `Player` ≈ 100), **not** automatic batching of invocations.
- `%*` only works when Explorer passes all selections in a single call — this is **unreliable** for legacy verbs (Variant F3).
- For reliable multi-file batching, use [context-menu-launcher](https://github.com/owenstake/context-menu-launcher) (`singleinstance.exe`) as a wrapper (Variant F2). It collects parallel Explorer launches via IPC and starts your app **once** with `$files`.

See [references/context-menu-launcher.md](references/context-menu-launcher.md) for full F2 integration details.

## Choose a Variant

| Question | Answer → Variant |
|---|---|
| Target is a **folder**? | **D** — Directory context menu |
| Target is **files**, one process per file? | **F1** — per-file with `%1` |
| Target is **files**, all in one run? | **F2** — context-menu-launcher (recommended) |
| Target is files, simple `%*` only (tested)? | **F3** — direct `%*` (limited) |

Copy-paste examples for all variants: [references/registry-entries.example.txt](references/registry-entries.example.txt)

---

### Variant D — Directory context menu

**When:** Tool operates on **one selected folder** (batch job in directory, scan folder, …).

**Registry key:** `HKEY_CLASSES_ROOT\Directory\shell\`

**Java:**

```java
addDirectoryCommandToRegistry(lines, "Run {{APP_DISPLAY_NAME}}", commandFile,
        "-run", "--{{CONFIG_PREFIX}}.source-folder=%1");
```

**`.reg`:**

```reg
[HKEY_CLASSES_ROOT\Directory\shell\Run My-App]
@="Run My-App"

[HKEY_CLASSES_ROOT\Directory\shell\Run My-App\command]
@="\"D:\\myapp\\MyApp-Registry.cmd\" \"-run\" \"--myapp.source-folder=%1\""
```

**Notes:** `%1` = folder path; `MultiSelectModel` not required (only one folder selectable); wrapper `.cmd` sets `cd` to install dir.

Snippet: [references/registry-variant-D-directory.java.snippet](references/registry-variant-D-directory.java.snippet)

---

### Variant F1 — Files, one process per file

**When:** **Separate** app starts per file (viewer, parallel single-file processing).

**Registry key:** `HKEY_CLASSES_ROOT\*\shell\`

**Java:**

```java
addFilePerFileCommandToRegistry(lines, "Run {{APP_DISPLAY_NAME}}", commandFile,
        "-run", "--files", "%1");
```

**`.reg`:**

```reg
[HKEY_CLASSES_ROOT\*\shell\Run My-App]
@="Run My-App"

[HKEY_CLASSES_ROOT\*\shell\Run My-App\command]
@="\"D:\\myapp\\MyApp-Registry.cmd\" \"-run\" \"--files\" \"%1\""
```

**Notes:** Omit `MultiSelectModel` or set `Document` — Explorer starts N processes for N files; `%1` contains **one** file each time; no launcher needed.

Snippet: [references/registry-variant-F1-per-file.java.snippet](references/registry-variant-F1-per-file.java.snippet)

---

### Variant F2 — Files, batched via context-menu-launcher (recommended)

**When:** Multi-select → **one** app start with all files (rename, batch analysis, …).

**Registry key:** `HKEY_CLASSES_ROOT\*\shell\`

**Java** (reference: smart-naming):

```java
File singleinstanceFile = new File(targetFolder, "../win-tools/singleinstance.exe");

addCommandToRegistry(lines, "Run {{APP_DISPLAY_NAME}}",
        quoted(singleinstanceFile.getCanonicalPath()),
        quoted("%1"),
        quoted(commandFile.getCanonicalPath()),
        "-run", "--files", quoted("\\\"$files\\\""), "--si-timeout", "400");
```

**`.reg`:**

```reg
[HKEY_CLASSES_ROOT\*\shell\Run My-App]
@="Run My-App"
"MultiSelectModel"="Player"

[HKEY_CLASSES_ROOT\*\shell\Run My-App\command]
@="\"D:\\myapp\\win-tools\\singleinstance.exe\" \"%1\" \"D:\\myapp\\MyApp-Registry.cmd\" -run --files \"$files\" --si-timeout 400"
```

**Notes:**

- `"%1"` **between** `singleinstance.exe` and wrapper — **required** (Explorer passes one file per parallel launch)
- `$files` = aggregated file list from launcher (single argument, single-quoted paths)
- `MultiSelectModel=Player` — raises selection limit
- Bundle `singleinstance.exe` from [context-menu-launcher](https://github.com/owenstake/context-menu-launcher) in `win-tools/` — see [references/assembly-win-tools.xml.snippet](references/assembly-win-tools.xml.snippet)
- Runner: `correctSingleArg()` to parse `$files` — see [references/Runner-correctSingleArg.java.template](references/Runner-correctSingleArg.java.template)

Snippet: [references/registry-variant-F2-launcher.java.snippet](references/registry-variant-F2-launcher.java.snippet)

---

### Variant F3 — Files, direct `%*` (simple, limited)

**When:** Only if explicitly tested on target Windows versions; often unreliable for multi-select.

**Registry key:** `HKEY_CLASSES_ROOT\*\shell\`

**Java:**

```java
addFileBatchedDirect(lines, "Run {{APP_DISPLAY_NAME}}", commandFile,
        "-run", "--files", "%*");
```

**`.reg`:**

```reg
[HKEY_CLASSES_ROOT\*\shell\Run My-App]
@="Run My-App"
"MultiSelectModel"="Player"

[HKEY_CLASSES_ROOT\*\shell\Run My-App\command]
@="\"D:\\myapp\\MyApp-Registry.cmd\" \"-run\" \"--files\" %*"
```

**Notes:** `%*` must stay **unquoted**; `MultiSelectModel=Player` required; prefer **F2** when Explorer still launches per file.

Snippet: [references/registry-variant-F3-direct-percent-star.java.snippet](references/registry-variant-F3-direct-percent-star.java.snippet)

---

## Setup Steps

1. Generate Java classes from templates:
   - `WindowsRegistryConfigs.java.template` → `config/WindowsRegistryConfigs.java`
   - `GenerateWindowsRegistryEntries.java.template` → `core/GenerateWindowsRegistryEntries.java`
   - `ImportWindowsRegistry.java.template` → `core/ImportWindowsRegistry.java`
2. Replace `{{REGISTRY_ENTRIES}}` in `GenerateWindowsRegistryEntries` with the chosen variant from [references/registry-entries.example.txt](references/registry-entries.example.txt).
3. Add shell scripts:
   - `generateRegistry.cmd.template` → `src/main/assembly/shell/generateWindowsRegistryEntries.cmd`
   - `installRegistry.cmd.template` → `src/main/assembly/shell/installWindowsRegistry.cmd`
4. Extend `{{RUNNER_CLASS}}` with registry CLI options using [references/CommandLineRunner-registry.java.template](references/CommandLineRunner-registry.java.template).
5. Add `--files` option to runner (required with `--run` for file variants) — see `cli-tool-runner`.
6. For **F2**: add `correctSingleArg()` to runner; bundle `win-tools/singleinstance.exe` in assembly.
7. Add registry properties to `application.properties` (see `cli-tool-runner` template).

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

For file variants, call `correctSingleArg()` before processing `--files` (required for F2).

Inject `GenerateWindowsRegistryEntries` and `ImportWindowsRegistry` via `@Resource`.

## `%*` Quoting Rule (Variant F3 only)

When the last argument is `%*`, it must remain **unquoted** in the registry command line so Explorer can expand all selected file paths:

```
"\"...\MyApp-Registry.cmd\" \"-run\" \"--files\" %*"
```

`buildRegistryCommandLine()` in the template handles this when the last arg is `%*`.

## Generated Files

| File | Purpose |
|---|---|
| `{{APP_NAME}}-Registry.cmd` | Wrapper: `cd` to install dir, call `{{SHELL_SCRIPT_NAME}}.cmd` |
| `{{APP_NAME}}-Install.reg` | Registry entries under `HKEY_CLASSES_ROOT\*\shell\` or `Directory\shell\` |
| `win-tools/singleinstance.exe` | F2 only — context-menu-launcher binary |

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
- F2: missing `singleinstance.exe` in `win-tools/` — validate at generation time or document download step