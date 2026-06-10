# JavaDoc Examples

## Service method with return value

```java
/**
 * Runs smart naming for the given files: uploads or inlines content, requests LLM suggestions,
 * validates the response, and renames files on disk.
 *
 * <p>Example input: {@code [new File("photo.jpg"), new File("scan.pdf")]}
 * <br>Example output: {@code {"photo.jpg": "2024-01-15_vacation_beach.jpg", "scan.pdf": "2024-01-15_invoice_acme.pdf"}}
 *
 * @param files readable regular files to rename
 * @return map of original filename to suggested filename (before physical rename)
 * @throws SmartNamingException if validation, LLM request, or rename fails
 */
public Map<String, String> run(List<File> files) { ... }
```

## Parser with Optional return

```java
/**
 * Parses and validates an LLM naming response as a JSON map of original to suggested filenames.
 *
 * <p>Example input: {@code {"old.jpg": "2024-01-15_beach.jpg"}}
 * <br>Example output: {@code Optional.of({"old.jpg": "2024-01-15_beach.jpg"})}
 * <br>Invalid JSON or validation failure: {@code Optional.empty()}
 *
 * @param rawResponse assistant message content from the chat completion
 * @param inputFiles files that were sent to the model (used for key validation)
 * @return validated suggestions, or empty when the response cannot be parsed
 * @throws SmartNamingException when the JSON is parseable but fails validation rules
 */
public Optional<Map<String, String>> parse(String rawResponse, List<File> inputFiles) { ... }
```

## void method with side effects

```java
/**
 * Physically renames source files according to the suggestion map.
 *
 * <p>Example: source {@code /tmp/photo.jpg}, map {@code {"photo.jpg": "2024-01-15_beach.jpg"}}
 * <br>Result: file moved to {@code /tmp/2024-01-15_beach.jpg}
 *
 * @param sourceFiles files to rename (parent directory must be writable)
 * @param suggestionsByOriginalName map keyed by {@link File#getName()}
 * @throws SmartNamingException if a suggestion is missing, target exists, or IO fails
 */
public void renameFiles(List<File> sourceFiles, Map<String, String> suggestionsByOriginalName) { ... }
```

## Configuration resolver

```java
/**
 * Resolves the LLM model configuration for the given key.
 *
 * <p>Example input: {@code "lm-studio"}
 * <br>Example output: configured {@link LlmModelConfig} for that key
 *
 * @param modelKey key from {@code smartnaming.models.*}
 * @return model configuration
 * @throws SmartNamingException if the key is blank or unknown
 */
public LlmModelConfig resolveModel(String modelKey) { ... }
```

## CLI entry point

```java
/**
 * Parses CLI arguments and dispatches to run, registry generation, install, or help.
 *
 * <p>Example: {@code -run --files photo.jpg scan.pdf}
 * <br>Example (context-menu-launcher): {@code -run --files 'photo.jpg' 'scan.pdf'}}
 *
 * @param args command-line arguments passed to the Spring Boot application
 */
public void run(String... args) { ... }
```