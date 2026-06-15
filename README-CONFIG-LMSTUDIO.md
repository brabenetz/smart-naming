# Configuration — LM Studio (local qwen3.5-4b)

Run Smart-Naming against a **local** OpenAI-compatible server with [LM Studio](https://lmstudio.ai/).

Back to [README.md](README.md).

## Prerequisites

- **GPU with sufficient VRAM** for the chosen model. For `qwen3.5-4b` (~4B parameters), a consumer GPU with several GB VRAM is typically enough; exact requirements depend on quantization and context length.
- LM Studio installed on the same machine as Smart-Naming (or reachable on the network).

## Step-by-step setup

### 1. Install LM Studio

Download and install from [lmstudio.ai](https://lmstudio.ai/).

Video walkthrough (general LM Studio usage): [YouTube — LM Studio guide](https://www.youtube.com/watch?v=UngVdAsQEiU)

### 2. Download the model

1. Open LM Studio.
2. Go to the model search / discovery tab.
3. Search for **`qwen3.5-4b`** (or the exact variant you prefer, e.g. Q4 quantization).
4. Download the model to your local cache.

### 3. Load the model and start the server

1. Open the **Local Server** (or **Developer** / **Serve**) tab.
2. Load `qwen3.5-4b`.
3. Start the server — default URL is usually:

   ```
   http://localhost:1234
   ```

4. Note the served model identifier shown in LM Studio (use the same string in Smart-Naming config).

### 4. Configure Smart-Naming

Edit `config/application.yml`:

```yaml
smartnaming:
  used-model: local-qwen
  models:
    local-qwen:
      url: http://localhost:1234/v1
      model: qwen3.5-4b
      file-delivery: inline-image
```

**Why `inline-image`?** LM Studio does not expose OpenAI's `/v1/files` upload API. Smart-Naming encodes images as base64 and sends them inline in the chat request — the standard workaround for local servers.

### 5. API token (optional)

Many local servers accept any token or none. If required, add to `config/application.properties`:

```properties
smartnaming.models.local-qwen.auth.api-token=lm-studio
```

Or via environment variable:

```text
SMARTNAMING_MODELS_LOCAL-QWEN_AUTH_API-TOKEN=lm-studio
```

### 6. Test

1. Ensure the LM Studio server is running.
2. Right-click a test image in File Explorer → **Run Smart-Naming**,  
   **or** run [SmartNamingManuellTest.java](src/test/java/net/brabenetz/tools/smart/naming/SmartNamingManuellTest.java) from the IDE with `used-model: local-qwen` in `application-manual.yml`.

## Troubleshooting

| Symptom | Check |
|---------|-------|
| Connection refused | LM Studio server started? URL `http://localhost:1234/v1`? |
| Slow responses | Normal for local models; smaller quantizations are faster |
| PDF / non-image errors | `inline-image` supports images; PDFs need `upload` mode on APIs that support it |
| Invalid filename | Align [system prompt](README-CONFIG-SYSTEMPROMPT.md) with `target-filename-pattern` |

## See also

- [README-CONFIG-SYSTEMPROMPT.md](README-CONFIG-SYSTEMPROMPT.md)
- [README-CONFIG-GROK.md](README-CONFIG-GROK.md)