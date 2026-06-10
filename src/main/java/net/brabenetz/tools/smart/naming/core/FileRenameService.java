package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class FileRenameService {

    private static final Logger LOG = LoggerFactory.getLogger(FileRenameService.class);

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
    public void renameFiles(List<File> sourceFiles, Map<String, String> suggestionsByOriginalName) {
        for (File sourceFile : sourceFiles) {
            String originalName = sourceFile.getName();
            String suggestedName = suggestionsByOriginalName.get(originalName);
            if (StringUtils.isBlank(suggestedName)) {
                throw new SmartNamingException(String.format("Missing rename suggestion for file '%s'", originalName));
            }
            renameFile(sourceFile, suggestedName);
        }
    }

    private void renameFile(File sourceFile, String suggestedName) {
        if (sourceFile.getName().equals(suggestedName)) {
            LOG.info("  already named: {}", suggestedName);
            return;
        }
        if (suggestedName.contains("/") || suggestedName.contains("\\")) {
            throw new SmartNamingException(String.format(
                    "Suggestion must be a filename, not a path: %s", suggestedName));
        }

        File parentDirectory = sourceFile.getParentFile();
        if (parentDirectory == null || !parentDirectory.exists() || !parentDirectory.canWrite()) {
            throw new SmartNamingException(String.format(
                    "Directory is not writable for rename: %s", sourceFile.getAbsolutePath()));
        }

        File targetFile = new File(parentDirectory, suggestedName);
        if (targetFile.exists()) {
            throw new SmartNamingException(String.format(
                    "Target file already exists, refusing to overwrite: %s", targetFile.getAbsolutePath()));
        }
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new SmartNamingException(String.format(
                    "Source file does not exist or is not a regular file: %s", sourceFile.getAbsolutePath()));
        }

        try {
            FileUtils.moveFile(sourceFile, targetFile);
            LOG.info("  renamed: {} -> {}", sourceFile.getName(), suggestedName);
        } catch (IOException e) {
            throw new SmartNamingException(String.format(
                    "Failed to rename '%s' to '%s'", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()), e);
        }
    }
}