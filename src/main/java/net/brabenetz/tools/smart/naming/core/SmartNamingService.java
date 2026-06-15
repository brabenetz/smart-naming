package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.config.LlmModelConfig;
import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SmartNamingService {

    private static final Logger LOG = LoggerFactory.getLogger(SmartNamingService.class);

    @Resource
    private SmartNamingConfigs smartNamingConfigs;
    @Resource
    private LlmFileContentBuilder llmFileContentBuilder;
    @Resource
    private OpenAiNamingClient openAiNamingClient;
    @Resource
    private FileNamingResponseParser fileNamingResponseParser;
    @Resource
    private FileRenameService fileRenameService;

    /**
     * Runs smart naming for the given files: prepares content for the LLM, requests suggestions
     * with retries, validates the response, and renames files on disk.
     *
     * <p>optionally skipping physical rename:
     * <br>When {@code simulate} is {@code true}, suggestions are requested and logged but
     * {@link FileRenameService} is not called.
     *
     * <p>Example input: {@code [new File("photo.jpg"), new File("scan.pdf")]}
     * <br>Example output: {@code {"photo.jpg": "2024-01-15_vacation_beach.jpg", "scan.pdf": "2024-01-15_invoice_acme.pdf"}}
     *
     * @param files readable regular files to process
     * @param simulate when {@code true}, skip file rename (dry-run)
     * @return map of original filename to suggested filename
     * @throws SmartNamingException if validation, LLM request, or rename fails
     */
    public Map<String, String> run(List<File> files, boolean simulate) {
        validateFiles(files);
        LlmModelConfig activeModel = smartNamingConfigs.resolveActiveModel();
        String systemPrompt = smartNamingConfigs.getSystemPrompt();
        if (StringUtils.isBlank(systemPrompt)) {
            throw new SmartNamingException("smartnaming.system-prompt is not configured");
        }
        String targetFilenamePattern = smartNamingConfigs.getTargetFilenamePattern();
        String effectiveSystemPrompt = systemPrompt
                + "\n\nDie target-Filenames müssen der Expression "
                + targetFilenamePattern
                + " entsprechen.";
        LOG.debug("Using System-Prompt: {}", effectiveSystemPrompt);

        LOG.info("Running Smart-Naming for {} file(s) with model key '{}': url={}, model={}",
                files.size(), smartNamingConfigs.getUsedModel(), activeModel.getUrl(), activeModel.getModel());

        List<UploadedFileReference> uploadedFiles = llmFileContentBuilder.prepareFiles(activeModel, files);
        uploadedFiles.forEach(uploaded -> {
            if (uploaded.usesFileUpload()) {
                LOG.info("  uploaded: {} -> {}", uploaded.getOriginalFileName(), uploaded.getFileId());
            } else {
                LOG.info("  inline image prepared: {}", uploaded.getOriginalFileName());
            }
        });

        Map<String, String> suggestions = requestSuggestionsWithRetry(activeModel, effectiveSystemPrompt, uploadedFiles, files);
        suggestions.forEach((original, suggested) -> LOG.info("  suggestion: {} -> {}", original, suggested));

        if (simulate) {
            LOG.info("Simulate mode: skipping file rename");
        } else {
            fileRenameService.renameFiles(files, suggestions);
        }
        return suggestions;
    }

    private Map<String, String> requestSuggestionsWithRetry(LlmModelConfig activeModel, String systemPrompt,
            List<UploadedFileReference> uploadedFiles, List<File> inputFiles) {
        int maxRetries = smartNamingConfigs.getMaxRetries();
        String lastFailureReason = "unknown";

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String rawResponse = openAiNamingClient.requestNamingSuggestions(activeModel, systemPrompt, uploadedFiles);
                Optional<Map<String, String>> parsed = fileNamingResponseParser.parse(rawResponse, inputFiles);
                if (parsed.isPresent()) {
                    return parsed.get();
                }
                lastFailureReason = "response is not valid JSON or failed validation";
            } catch (SmartNamingException e) {
                lastFailureReason = e.getMessage();
            } catch (Exception e) {
                lastFailureReason = e.getMessage();
            }
            LOG.warn("Naming response invalid (attempt {}/{}): {}", attempt, maxRetries, lastFailureReason);
        }

        throw new SmartNamingException(String.format(
                "Failed to obtain valid naming suggestions after %d attempts. Last error: %s",
                maxRetries, lastFailureReason));
    }

    private void validateFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            throw new SmartNamingException("At least one file is required");
        }
        for (File file : files) {
            if (file == null || !file.exists() || !file.isFile()) {
                throw new SmartNamingException(String.format("File does not exist or is not a regular file: %s", file));
            }
            if (!file.canRead()) {
                throw new SmartNamingException(String.format("File is not readable: %s", file.getAbsolutePath()));
            }
        }
    }
}