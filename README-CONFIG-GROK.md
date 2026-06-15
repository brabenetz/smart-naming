# Configuration — Grok (x.ai)

Use [Grok](https://x.ai/) as the naming model via the x.ai OpenAI-compatible API.

Back to [README.md](README.md).

## Step 1 — Create an API token

1. Sign in at [console.x.ai](https://console.x.ai).
2. Open the **API keys** (or developer / credentials) section.
3. Create a new API key and copy the token (starts with `xai-...`).
4. Store it securely — it is shown only once.

## Step 2 — Store the token

**Recommended:** `config/application.properties` in your Smart-Naming install folder:

```properties
smartnaming.models.grok.auth.api-token=xai-your-key-here
```

On first startup, the plain-text value is encrypted automatically by [secured-properties](https://github.com/brabenetz/secured-properties). On subsequent runs, the encrypted value is used as-is.

**Alternative — environment variable:**

```text
SMARTNAMING_MODELS_GROK_AUTH_API-TOKEN=xai-your-key-here
```

## Step 3 — Configure the model

Edit `config/application.yml`:

```yaml
smartnaming:
  used-model: grok
  models:
    grok:
      url: https://api.x.ai/v1
      model: grok-4.3
      file-delivery: inline-image
```

| Property | Value |
|----------|-------|
| `url` | x.ai API base (`/v1` suffix required) |
| `model` | Model name as listed in x.ai documentation (e.g. `grok-4.3`) |
| `file-delivery` | `inline-image` — images sent inline; adjust if x.ai file upload is used |

Set `smartnaming.used-model: grok` to activate this entry.

## Step 4 — Test

1. Save `application.yml` and `application.properties`.
2. Right-click a document in File Explorer → **Run Smart-Naming**.

For development testing, use [SmartNamingManuellTest.java](src/test/java/net/brabenetz/tools/smart/naming/SmartNamingManuellTest.java) with `used-model: grok` in `application-manual.yml`.

## Troubleshooting

| Symptom | Check |
|---------|-------|
| 401 Unauthorized | Token correct? Encrypted value corrupted? Re-enter plain token in `application.properties` |
| Model not found | `model` name matches x.ai console / docs |
| Invalid filename | See [README-CONFIG-SYSTEMPROMPT.md](README-CONFIG-SYSTEMPROMPT.md) |

## See also

- [README-CONFIG-SYSTEMPROMPT.md](README-CONFIG-SYSTEMPROMPT.md)
- [README-CONFIG-LMSTUDIO.md](README-CONFIG-LMSTUDIO.md)
- [README-DEV.md](README-DEV.md)