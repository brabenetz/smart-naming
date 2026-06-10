---
name: java-documentation
description: >
  Add and review JavaDoc and inline comments in Java code. All comments must be English.
  Public/protected methods with more than three executable body lines require JavaDoc with
  @param/@return/@throws and input-output examples when meaningful. Triggers: JavaDoc, Javadoc,
  inline comment, code documentation, Kommentare, /java-documentation.
---

# Java Documentation

Standards for JavaDoc and inline comments in Java source files.

## Language

All comments must be **English**: JavaDoc blocks, `//` line comments, `/* */` block comments, and tag descriptions (`@param`, `@return`, `@throws`).

User-facing strings, LLM prompts, and log messages are out of scope unless the user explicitly asks to translate them.

## JavaDoc Rules

### When JavaDoc is required

Every **public** or **protected** method whose method body contains **more than 3 executable lines** (exclude `{`, `}`, and blank lines).

### Exceptions (no JavaDoc required)

- Single-line getters/setters (`return field;`, `this.field = value;`)
- `main()` and trivial delegations (≤3 executable lines)
- `@Override` methods that only call `super` (≤3 executable lines)
- Private and package-private methods (use inline comments only when needed)

### JavaDoc content

1. One-sentence summary of purpose
2. `@param` for each parameter
3. `@return` when not void
4. `@throws` for checked exceptions and documented runtime exceptions
5. **Input/output examples** when the method transforms, parses, or validates non-trivial data

Example format for transformations:

```java
/**
 * Parses a single CLI argument that may contain multiple single-quoted file paths.
 *
 * <p>Example input: {@code 'file 1.png' 'file 2.png'}
 * <br>Example output: {@code ["file 1.png", "file 2.png"]}
 *
 * @param filePaths raw values from Commons CLI
 * @return individual file paths; unchanged when input is not a quoted list
 */
```

More templates: [references/javadoc-examples.md](references/javadoc-examples.md)

## Inline Comment Rules

- **English only**
- Explain **why**, not obvious **what**
- Keep comments brief; prefer clear method names over comments
- Allowed for: non-obvious regex, workarounds, protocol quirks, security-sensitive steps
- Avoid: commented-out code, restating the next line of code

Good vs. bad examples: [references/inline-comment-examples.md](references/inline-comment-examples.md)

## Workflow

1. Scan target Java files (default: `src/main/java`)
2. List public/protected methods with >3 executable body lines
3. Add or enhance JavaDoc (include examples where meaningful)
4. Review existing inline comments — translate to English, remove redundant ones, add where logic is complex
5. Do **not** change behavior — documentation only
6. Run `mvn test` to verify the build

## Counting executable lines

Count non-blank lines inside the method body that are not sole `{` or `}`. Example — this method has **4** executable lines and **requires** JavaDoc:

```java
public void renameFiles(List<File> files, Map<String, String> map) {
    for (File file : files) {           // 1
        String name = file.getName();   // 2
        String target = map.get(name);  // 3
        rename(file, target);           // 4
    }
}
```