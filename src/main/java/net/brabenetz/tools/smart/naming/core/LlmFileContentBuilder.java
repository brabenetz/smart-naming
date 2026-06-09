package net.brabenetz.tools.smart.naming.core;

import com.openai.client.OpenAIClient;
import com.openai.models.files.FileCreateParams;
import com.openai.models.files.FileObject;
import com.openai.models.files.FilePurpose;
import net.brabenetz.tools.smart.naming.config.FileDeliveryMode;
import net.brabenetz.tools.smart.naming.config.LlmModelConfig;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class LlmFileContentBuilder {

    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"));
    private static final Set<String> PDF_EXTENSIONS = new HashSet<>(Arrays.asList("pdf"));
    private static final Map<String, String> IMAGE_MIME_TYPES = new HashMap<>();

    static {
        IMAGE_MIME_TYPES.put("jpg", "image/jpeg");
        IMAGE_MIME_TYPES.put("jpeg", "image/jpeg");
        IMAGE_MIME_TYPES.put("png", "image/png");
        IMAGE_MIME_TYPES.put("gif", "image/gif");
        IMAGE_MIME_TYPES.put("webp", "image/webp");
    }

    @Resource
    private OpenAiClientFactory openAiClientFactory;

    public List<UploadedFileReference> prepareFiles(LlmModelConfig modelConfig, List<File> files) {
        if (modelConfig.getFileDelivery() == FileDeliveryMode.INLINE_IMAGE) {
            List<UploadedFileReference> inlineFiles = new ArrayList<>();
            for (File file : files) {
                inlineFiles.add(buildInlineImageReference(file));
            }
            return inlineFiles;
        }

        OpenAIClient client = openAiClientFactory.createClient(modelConfig);
        List<UploadedFileReference> uploaded = new ArrayList<>();
        for (File file : files) {
            uploaded.add(uploadFile(client, file));
        }
        return uploaded;
    }

    private UploadedFileReference uploadFile(OpenAIClient client, File file) {
        FilePurpose purpose = resolvePurpose(file);
        FileCreateParams params = FileCreateParams.builder()
                .file(Paths.get(file.getAbsolutePath()))
                .purpose(purpose)
                .build();
        FileObject fileObject = client.files().create(params);
        return new UploadedFileReference(file, fileObject.id());
    }

    private UploadedFileReference buildInlineImageReference(File file) {
        String extension = normalizeExtension(file);
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw new SmartNamingException(String.format(
                    "Model delivery mode 'inline-image' supports images only, but got '.%s' for %s. "
                            + "Use file-delivery: upload for PDFs or OpenAI-compatible file uploads.",
                    extension, file.getName()));
        }
        try {
            byte[] content = FileUtils.readFileToByteArray(file);
            String base64 = Base64.getEncoder().encodeToString(content);
            String dataUrl = "data:" + IMAGE_MIME_TYPES.get(extension) + ";base64," + base64;
            return new UploadedFileReference(file, null, dataUrl);
        } catch (IOException e) {
            throw new SmartNamingException(String.format("Failed to read file for inline delivery: %s", file.getAbsolutePath()), e);
        }
    }

    private FilePurpose resolvePurpose(File file) {
        String extension = normalizeExtension(file);
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return FilePurpose.VISION;
        }
        if (PDF_EXTENSIONS.contains(extension)) {
            return FilePurpose.ASSISTANTS;
        }
        throw new SmartNamingException(String.format(
                "Unsupported file type '.%s' for %s. Supported: images (%s) and pdf.",
                extension, file.getName(), IMAGE_EXTENSIONS));
    }

    private String normalizeExtension(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        if (extension == null) {
            throw new SmartNamingException(String.format("File has no extension: %s", file.getAbsolutePath()));
        }
        return extension.toLowerCase(Locale.ROOT);
    }
}