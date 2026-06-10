# context-menu-launcher Integration (Variant F2)

[context-menu-launcher](https://github.com/owenstake/context-menu-launcher) (`singleinstance.exe`) solves the Windows Explorer multi-file problem for legacy registry shell verbs.

## Problem

When a user selects multiple files and clicks a context menu entry, Explorer typically launches the command **once per file**. Your application is responsible for coalescing these parallel starts into a single run — or you accept one process per file (Variant F1).

`MultiSelectModel=Player` with `%*` (Variant F3) does **not** reliably fix this for legacy verbs.

## How singleinstance.exe Works

1. Explorer launches `singleinstance.exe "%1" {command} $files [args]` once per selected file.
2. Each instance receives `%1` = one file path from Explorer.
3. The first instance waits; subsequent instances notify the first via IPC.
4. After `--si-timeout` milliseconds, the first instance launches `{command}` **once** with `$files` = all collected paths (single-quoted, space-separated).

Official syntax from the project README:

```
Usage: singleinstance.exe "%1" {command} $files [arguments]

Optional arguments for singleinstance (not passed to command):
--si-timeout {time to wait in msecs}
```

## Registry Command Structure

```reg
[HKEY_CLASSES_ROOT\*\shell\Run My-App]
@="Run My-App"
"MultiSelectModel"="Player"

[HKEY_CLASSES_ROOT\*\shell\Run My-App\command]
@="\"D:\\myapp\\win-tools\\singleinstance.exe\" \"%1\" \"D:\\myapp\\MyApp-Registry.cmd\" -run --files \"$files\" --si-timeout 400"
```

| Part | Role |
|------|------|
| `singleinstance.exe` | Launcher — collects parallel Explorer invocations |
| `"%1"` | **Required** — file path Explorer passes to each parallel launch |
| `MyApp-Registry.cmd` | Wrapper — `cd` to install dir, calls `myapp.cmd` |
| `$files` | Placeholder replaced by launcher with aggregated file list |
| `--si-timeout 400` | Wait 400 ms for all Explorer launches to arrive |

**Critical:** `"%1"` must sit **between** `singleinstance.exe` and the wrapper/command path.

## Distribution

1. Download `singleinstance.exe` from [releases](https://github.com/owenstake/context-menu-launcher/releases).
2. Place in `src/main/assembly/win-tools/singleinstance.exe`.
3. Add assembly fileSet — see [assembly-win-tools.xml.snippet](assembly-win-tools.xml.snippet).
4. Reference path in `GenerateWindowsRegistryEntries`:

```java
File singleinstanceFile = new File(targetFolder, "../win-tools/singleinstance.exe");
```

License: Apache-2.0.

## Runner: Parsing `$files`

The launcher passes `$files` as **one** CLI argument containing multiple single-quoted paths, e.g.:

```
'C:\photos\img1.jpg' 'C:\photos\img2.png'
```

Add `correctSingleArg()` to the runner and call it before processing `--files`:

```java
String[] filePaths = cmd.getOptionValues("files");
filePaths = correctSingleArg(filePaths);
```

Full implementation: [Runner-correctSingleArg.java.template](Runner-correctSingleArg.java.template).

## Timeout Tuning

- `--si-timeout 400` works for typical multi-select (3–20 files).
- Increase if users report missing files on slow systems or very large selections.
- Too high: noticeable delay before the app starts.

## Testing Checklist

- [ ] Single file: one CMD window, app receives one path
- [ ] Multiple files: one CMD window, app receives all paths
- [ ] Files with spaces and apostrophes in names
- [ ] Re-install registry after changing `.reg` generation