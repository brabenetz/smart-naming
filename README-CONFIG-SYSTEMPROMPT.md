# Configuration — System Prompt and Filename Pattern

Customize how the LLM names your files.

Back to [README.md](README.md).

## Default system prompt

The current default `system-prompt` and `target-filename-pattern` are maintained in the repository at [`src/main/resources/application.yml`](src/main/resources/application.yml). That file is the **authoritative, up-to-date** source when you develop or browse the project.

The same content is **bundled into the JAR** at build time. In an installed distribution you can read it without the source tree:

```
lib/smart-naming-*.jar
  └── BOOT-INF/classes/application.yml
```

Open the JAR with [7-Zip](https://www.7-zip.org/) (or any ZIP tool) and copy the `system-prompt` block. For customization, either source is fine — use whichever is easier for you.

## Override locally

Add or extend in your installation `config/application.yml`:

```yaml
smartnaming:
  target-filename-pattern: '^\d{4}-\d{2}-\d{2}_[^_]+_[^_]+(?:_[\d,]+[A-Za-z]{3})?(?:_\(\d+\))?\.[A-Za-z0-9]+$'
  system-prompt: |
    You are a document naming assistant...
    (your customized instructions)
```

External configuration **merges with** JAR defaults; properties you set override the bundled values.

## Filename pattern (`target-filename-pattern`)

- Must be a **valid Java regular expression**.
- Every LLM suggestion is validated against this pattern before rename.
- The pattern is also appended to the effective system prompt at runtime so the model knows the expected format.

**Important:** the rules in `system-prompt` and the regex in `target-filename-pattern` must **describe the same naming scheme**. If they disagree, the model may produce valid-looking names that fail validation.

## Example mismatch to avoid

| system-prompt says | pattern allows | Result |
|--------------------|----------------|--------|
| `DD-MM-YYYY` date format | `^\d{4}-\d{2}-\d{2}_...` (ISO date) | Suggestions rejected |

After changes, test with a single file from the context menu or [SmartNamingManuellTest.java](src/test/java/net/brabenetz/tools/smart/naming/SmartNamingManuellTest.java).

## Related settings

| Property | Default | Purpose |
|----------|---------|---------|
| `smartnaming.max-retries` | `3` | Re-request from LLM when response is invalid |
| `smartnaming.target-filename-pattern` | see [`src/main/resources/application.yml`](src/main/resources/application.yml) | Regex validation |
| `smartnaming.system-prompt` | see [`src/main/resources/application.yml`](src/main/resources/application.yml) | LLM instructions |

## See also

- [README-CONFIG-LMSTUDIO.md](README-CONFIG-LMSTUDIO.md)
- [README-CONFIG-GROK.md](README-CONFIG-GROK.md)