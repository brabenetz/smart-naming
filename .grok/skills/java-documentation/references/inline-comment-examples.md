# Inline Comment Examples

## Good — explains why

```java
// context-menu-launcher passes all files as one single-quoted argument
filePaths = correctSingleArg(filePaths);
```

```java
// reg import reports success on stderr on some Windows versions
errorLines.forEach(LOG::info);
```

```java
// Apostrophe inside filename is allowed when not followed by whitespace (next file boundary)
Pattern pattern = Pattern.compile("'((?:[^']|'[^\\s])*)'");
```

```java
// %* must stay unquoted in the .reg file so Explorer expands all selected paths
return quotedPart + " %*";
```

## Bad — restates obvious code

```java
// Loop over files
for (File file : files) {
```

```java
// Return the result
return parsed.get();
```

```java
// Set the used model
this.usedModel = usedModel;
```

## Bad — non-English (must translate)

```java
// Verschlüsselt Tokens beim Start
SecuredPropertiesHelper.encryptProperties(tokenKeys);
```

Correct:

```java
// Encrypt API tokens in application.properties at startup
SecuredPropertiesHelper.encryptProperties(tokenKeys);
```

## When to use inline vs JavaDoc

| Situation | Use |
|-----------|-----|
| Public/protected method, >3 lines | JavaDoc |
| Private helper, complex regex or protocol | Short inline comment |
| Obvious loop or assignment | No comment |
| Workaround for external tool behavior | Inline comment at the workaround |