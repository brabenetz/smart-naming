# Smart-Naming

Rename documents intelligently from the Windows File Explorer context menu using an OpenAI-compatible LLM.

## Usage

1. In File Explorer, select **one or more files**.
2. Right-click the selection.
3. Click **Run Smart-Naming**.

![Windows Context-Menue - Run SmartNaming](./img/windows-context-menu_run-smartnaming.gif)

Smart-Naming sends the files to your configured model, receives rename suggestions, validates them, and renames the files locally. When you select multiple files, they are processed in a **single run** (the context-menu wrapper batches Explorer invocations).

## Installation

### Download and extract

1. Download `smart-naming-*-dist-win.zip` from [GitHub Releases](https://github.com/brabenetz/smart-naming/releases).
2. Extract the archive to a folder of your choice, for example `C:\Tools\smart-naming`.

After extraction you should see:

```
smartnaming.cmd
installWindowsRegistry.cmd
generateWindowsRegistryEntries.cmd
config/application.yml
lib/smart-naming-*.jar
win-tools/singleinstance.exe
```

### Register the context menu

**Option A — install script (recommended)**

- Right-click `installWindowsRegistry.cmd` and choose **Run as administrator**,  
  **or** open a command prompt in the install folder and run:

  ```cmd
  smartnaming.cmd -i
  ```

**Option B — manual registry import**

1. Run `generateWindowsRegistryEntries.cmd` (or `smartnaming.cmd -gwre`).
2. Open the generated folder `windows-registry/`.
3. Double-click `SmartNaming-Install.reg` or import it with the Windows Registry Editor.

After installation, **Run Smart-Naming** appears in the context menu for files.

## Configuration

Edit `config/application.yml` in your installation folder. Example structure:

```yaml
smartnaming:
  used-model: grok
  models:
    local-qwen:
      url: http://localhost:1234/v1
      model: qwen3.5-4b
      file-delivery: inline-image
    grok:
      url: https://api.x.ai/v1
      model: grok-4.3
      file-delivery: inline-image
```

### Main properties

| Property | Description |
|----------|-------------|
| `smartnaming.used-model` | Active model key from `smartnaming.models` |
| `smartnaming.models.<key>.url` | OpenAI-compatible API base URL |
| `smartnaming.models.<key>.model` | Model name passed to the API |
| `smartnaming.models.<key>.file-delivery` | How files are sent to the API (see below) |
| `smartnaming.models.<key>.auth.api-token` | API token for this model (see token storage) |

### File delivery (`file-delivery`)

| Value | When to use |
|-------|-------------|
| `upload` | API supports OpenAI-style file upload via `/v1/files` |
| `inline-image` | API has **no** separate file upload (e.g. LM Studio). Files are **base64-encoded** and sent inline in the chat request as image data URLs — a common workaround for local OpenAI-compatible servers |

Most local implementations do not support a dedicated file-upload endpoint; use `inline-image` for those.

### API tokens

**Recommended:** store tokens in `config/application.properties` or `./application.properties` (next to `smartnaming.cmd`):

```properties
smartnaming.models.<key>.auth.api-token=your-token-here
```

On the first startup, plain-text values are **encrypted automatically** by [secured-properties](https://github.com/brabenetz/secured-properties). Replace `<key>` with your model key, e.g. `grok` or `local-qwen`.

**Environment variables:** any configuration property can also be set via environment variable (Spring Boot relaxed binding). Example for an API token:

```text
SMARTNAMING_MODELS_LOCAL-QWEN_AUTH_API-TOKEN=your-token-here
```

Use the model key in uppercase with hyphens (`local-qwen` → `LOCAL-QWEN`).

**Not recommended:** putting secrets directly in `application.yml`.

### Defaults (bundled in the JAR)

These can be overridden in `config/application.yml`:

- `smartnaming.max-retries` — retry count for invalid LLM responses
- `smartnaming.target-filename-pattern` — regex used to validate suggested filenames
- `smartnaming.system-prompt` — instructions sent to the LLM

The default values are inside the JAR (`BOOT-INF/classes/application.yml`). See [README-CONFIG-SYSTEMPROMPT.md](README-CONFIG-SYSTEMPROMPT.md) to customize the prompt.

### Advanced configuration

- [README-CONFIG-SYSTEMPROMPT.md](README-CONFIG-SYSTEMPROMPT.md) — custom system prompt and filename pattern
- [README-CONFIG-LMSTUDIO.md](README-CONFIG-LMSTUDIO.md) — local model with LM Studio (qwen3.5-4b)
- [README-CONFIG-GROK.md](README-CONFIG-GROK.md) — Grok via x.ai

## Third-party thanks

- **ASCII startup banner** — [patorjk Software TAAG](http://patorjk.com/software/taag/) (Big font)
- **Multi-file context menu** — [context-menu-launcher](https://github.com/owenstake/context-menu-launcher) (`singleinstance.exe`, Apache-2.0) for passing multiple selected files to one CLI invocation

## Additional links

- [README-DEV.md](README-DEV.md) — build, test, and release for developers