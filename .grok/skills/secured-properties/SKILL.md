---
name: secured-properties
description: >
  Integrate net.brabenetz.lib secured-properties into Brabenetz-style Spring Boot CLI tools:
  SecuredPropertiesHelper, encrypt secrets in application.properties at startup (AppPrepare),
  decrypt on demand without caching. Use when adding password encryption, API token security,
  SecuredPropertiesHelper, encryptProperties, decrypt, or runs /secured-properties.
---

# Secured Properties

Integrate [secured-properties](https://github.com/brabenetz/secured-properties) to encrypt secret values (passwords, API tokens) in external property files.

Official docs: https://github.com/brabenetz/secured-properties  
Spring Boot example: http://brabenetz.github.io/secured-properties/archiv/latest/exampleSpringBoot.html

## When to Use

- Brabenetz CLI tool needs encrypted secrets in `./config/application.properties` or `./application.properties`
- Called during `scaffold-cli-tool` / `cli-tool-runner` when the app has auth tokens or passwords
- User asks for `SecuredPropertiesHelper`, token encryption, or secured-properties integration

## Core Rules

1. **Encrypt once at startup** — call `SecuredPropertiesHelper.encryptProperties(...)` in `AppPrepare` (parent Spring context), before the main application uses secrets.
2. **Decrypt on demand** — call `SecuredPropertiesHelper.decrypt(value)` only where the secret is needed (e.g. HTTP client factory).
3. **Never cache decrypted values** — do not store decrypted secrets in fields, singletons, or config objects. Decrypt late, use immediately, let GC collect.
4. **Properties files only** — secured-properties reads/writes `.properties` files on disk. YAML in classpath is **not** auto-encrypted. Put encryptable secrets in external `application.properties` (assembly `config/` folder).

## Maven Dependency

```xml
<dependency>
    <groupId>net.brabenetz.lib</groupId>
    <artifactId>secured-properties</artifactId>
    <version>${dependency-secured-properties.version}</version>
</dependency>
```

Typical version property: `dependency-secured-properties.version` = `1.0-beta4`

## Step 1: Create SecuredPropertiesHelper

Generate from `references/SecuredPropertiesHelper.java.template` → `config/SecuredPropertiesHelper.java`.

Key points:

- `SecuredPropertiesConfig config = new SecuredPropertiesConfig().initDefault()`
- Default secret key: `%user_home%/.secret/securedProperties.key` (auto-created)
- Property files: `./config/application.properties`, `./application.properties` (same as Spring Boot external config)
- `encryptProperties(String... keys)` → `SecuredProperties.encryptNonEncryptedValues(config, propertyFiles, keys)`
- `decrypt(String value)` → decrypt only if `SecuredProperties.isEncryptedValue(value)`, else return as-is

## Step 2: Encrypt at Startup (Standard — Static Keys)

**Preferred / easy variant.** Call `encryptProperties` with **fixed property key names** in `AppPrepare.afterPropertiesSet()`:

```java
@EnableConfigurationProperties(MyAppConfigs.class)
public static class AppPrepare implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LOG.info("Current Java-Version: {}; OS: {}; ...", ...);

        SecuredPropertiesHelper.encryptProperties(
                "myapp.api-token",
                "myapp.database.password");
    }
}
```

Property file example (`src/main/assembly/config/application.properties`):

```properties
myapp.api-token=my-plain-token-here
```

After first run the file contains:

```properties
myapp.api-token={wVtvW8lQrwCf8MA9sadwww==}
```

`encryptNonEncryptedValues` is idempotent — already encrypted values are left unchanged.

### AppPrepare Parent Context

Use the parent Spring context pattern from `cli-tool-runner`:

```java
.parent(new SpringApplicationBuilder(AppPrepare.class)
    .banner(new MyAppBanner())
    .run(args))
```

`AppPrepare` **must** have `@EnableConfigurationProperties(...)` if encrypt keys are derived from config (see footnote). For static keys, `@EnableConfigurationProperties` is optional.

## Step 3: Decrypt Where Secrets Are Used

Decrypt at the last possible moment in the consuming class:

```java
String apiKey = "not-needed";
if (auth != null && StringUtils.isNotBlank(auth.getApiToken())) {
    apiKey = SecuredPropertiesHelper.decrypt(auth.getApiToken());
}
```

**Do not:**

- Decrypt in `@ConfigurationProperties` setter
- Cache decrypted value in a `private String cachedToken` field
- Log decrypted values

**Do:**

- Call `decrypt()` inline when building clients / connections
- Pass decrypted value only to the immediate API call

## Property Key Naming

Spring Boot relaxed binding maps property keys to Java fields:

| properties file | Java field |
|---|---|
| `myapp.api-token` | `apiToken` |
| `myapp.models.open-ai.auth.api-token` | nested `auth.apiToken` |

Use the **properties-file key format** (dot-separated, kebab-case) in `encryptProperties(...)`.

## Assembly / External Config

Ship a plain-text template in `src/main/assembly/config/application.properties`:

```properties
# User must set API token here (auto-encrypted on first run)
myapp.api-token=
```

Do **not** bundle encrypted secrets in the JAR. External config next to the distribution is the encryption target.

## Checklist

- [ ] `secured-properties` dependency in `pom.xml`
- [ ] `SecuredPropertiesHelper` in `config/` package
- [ ] `encryptProperties(...)` called in `AppPrepare.afterPropertiesSet()`
- [ ] `decrypt(...)` called at point of use (client factory, etc.)
- [ ] No permanent storage of decrypted values
- [ ] Secrets in external `application.properties`, not classpath YAML
- [ ] Assembly ships `config/application.properties` template

## Footnote: Dynamic Encrypt Keys (smart-naming pattern)

Use only when property keys depend on runtime configuration structure (e.g. a `models` map with arbitrary entries).

Requires `@EnableConfigurationProperties` on `AppPrepare` so config is bound **before** encrypt:

```java
@EnableConfigurationProperties(SmartNamingConfigs.class)
public static class AppPrepare implements InitializingBean {

    @Autowired
    private SmartNamingConfigs smartNamingConfigs;

    @Override
    public void afterPropertiesSet() {
        String[] tokenKeys = smartNamingConfigs.getModels().keySet().stream()
                .map(key -> "smartnaming.models." + key + ".auth.api-token")
                .toArray(String[]::new);
        SecuredPropertiesHelper.encryptProperties(tokenKeys);
    }
}
```

External properties example:

```properties
smartnaming.models.open-ai.auth.api-token=sk-...
```

**Caveats:**

- Parent context needs `@EnableConfigurationProperties` — otherwise `models` is empty
- Keys must match actual property paths in `application.properties`
- More complex than static keys — prefer static keys unless the config structure is dynamic

Reference implementation: `smart-naming` → `SecuredPropertiesHelper.java`, `SmartNamingCommandLineApplication.AppPrepare`, `OpenAiClientFactory.createClient()`.

## References

- `references/SecuredPropertiesHelper.java.template` — helper class template
- `references/AppPrepare-encrypt-static.java.template` — static-key encrypt in AppPrepare
- `references/decrypt-at-use.java.template` — decrypt pattern in service/factory