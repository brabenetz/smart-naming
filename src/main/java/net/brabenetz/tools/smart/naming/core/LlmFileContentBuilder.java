package net.brabenetz.tools.smart.naming.core;

import com.openai.client.OpenAIClient;
import com.openai.models.files.FileCreateParams;
import com.openai.models.files.FileObject;
import com.openai.models.files.FilePurpose;
import net.brabenetz.tools.smart.naming.config.LlmModelConfig;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class LlmFileContentBuilder {

    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"));
    private static final Set<String> PDF_EXTENSIONS = new HashSet<>(Arrays.asList("pdf"));

    @Resource
    private OpenAiClientFactory openAiClientFactory;

    public List<UploadedFileReference> uploadFiles(LlmModelConfig modelConfig, List<File> files) {
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

    private FilePurpose resolvePurpose(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        if (extension == null) {
            throw new SmartNamingException(String.format("File has no extension: %s", file.getAbsolutePath()));
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        if (IMAGE_EXTENSIONS.contains(normalized)) {
            return FilePurpose.VISION;
        }
        if (PDF_EXTENSIONS.contains(normalized)) {
            return FilePurpose.ASSISTANTS;
        }
        throw new SmartNamingException(String.format(
                "Unsupported file type '.%s' for %s. Supported: images (%s) and pdf.",
                normalized, file.getName(), IMAGE_EXTENSIONS));
    }
}