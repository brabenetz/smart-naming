package net.brabenetz.tools.smart.naming.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FileNamingResponseParser {

    private static final Pattern MARKDOWN_FENCE = Pattern.compile("^```(?:json)?\\s*|```\\s*$");
    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");

    @Resource
    private SmartNamingConfigs smartNamingConfigs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Map<String, String>> parse(String rawResponse, List<File> inputFiles) {
        if (StringUtils.isBlank(rawResponse)) {
            return Optional.empty();
        }
        try {
            String json = stripMarkdownFences(rawResponse.trim());
            Map<String, String> suggestions = objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, String>>() {
            });
            validateSuggestions(suggestions, inputFiles);
            return Optional.of(suggestions);
        } catch (Exception e) {
            if (e instanceof SmartNamingException) {
                throw (SmartNamingException) e;
            }
            return Optional.empty();
        }
    }

    private String stripMarkdownFences(String content) {
        String[] lines = content.split("\\R");
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            String stripped = MARKDOWN_FENCE.matcher(line).replaceAll("").trim();
            if (!stripped.isEmpty()) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(stripped);
            }
        }
        return builder.length() > 0 ? builder.toString() : content;
    }

    private void validateSuggestions(Map<String, String> suggestions, List<File> inputFiles) {
        if (suggestions == null || suggestions.isEmpty()) {
            throw new SmartNamingException("Naming response is empty");
        }
        List<String> expectedKeys = inputFiles.stream()
                .map(File::getName)
                .collect(Collectors.toList());
        for (String expectedKey : expectedKeys) {
            if (!suggestions.containsKey(expectedKey)) {
                throw new SmartNamingException(String.format("Missing suggestion for file '%s'", expectedKey));
            }
            String newName = suggestions.get(expectedKey);
            if (StringUtils.isBlank(newName)) {
                throw new SmartNamingException(String.format("Empty suggestion for file '%s'", expectedKey));
            }
            if (newName.contains("/") || newName.contains("\\")) {
                throw new SmartNamingException(String.format("Suggestion for '%s' must be a filename, not a path: %s", expectedKey, newName));
            }
            if (INVALID_FILENAME_CHARS.matcher(newName).find()) {
                throw new SmartNamingException(String.format("Suggestion for '%s' contains invalid characters: %s", expectedKey, newName));
            }
            if (!newName.contains(".")) {
                throw new SmartNamingException(String.format("Suggestion for '%s' must contain a file extension: %s", expectedKey, newName));
            }
            if (!smartNamingConfigs.getCompiledTargetFilenamePattern().matcher(newName).matches()) {
                throw new SmartNamingException(String.format(
                        "Suggestion for '%s' does not match required pattern %s: %s",
                        expectedKey, smartNamingConfigs.getTargetFilenamePattern(), newName));
            }
        }
        if (suggestions.size() != expectedKeys.size()) {
            throw new SmartNamingException(String.format(
                    "Expected %d suggestions but received %d", expectedKeys.size(), suggestions.size()));
        }
    }
}