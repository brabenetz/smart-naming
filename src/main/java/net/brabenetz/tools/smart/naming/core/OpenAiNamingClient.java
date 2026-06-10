package net.brabenetz.tools.smart.naming.core;

import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import net.brabenetz.tools.smart.naming.config.LlmModelConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OpenAiNamingClient {

    @Resource
    private OpenAiClientFactory openAiClientFactory;

    /**
     * Requests filename suggestions from an OpenAI-compatible chat completion API.
     *
     * <p>Builds a user message with the file list and attached files (upload IDs or inline images).
     * <br>Example output: {@code {"photo.jpg": "2024-01-15_beach.jpg"}}
     *
     * @param modelConfig URL, model name, and auth for the API client
     * @param systemPrompt instructions including target filename pattern
     * @param uploadedFiles prepared file references from {@link LlmFileContentBuilder}
     * @return raw assistant message content (may include markdown fences)
     */
    public String requestNamingSuggestions(LlmModelConfig modelConfig, String systemPrompt,
            List<UploadedFileReference> uploadedFiles) {
        OpenAIClient client = openAiClientFactory.createClient(modelConfig);
        List<ChatCompletionContentPart> contentParts = buildContentParts(uploadedFiles);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(modelConfig.getModel())
                .addSystemMessage(systemPrompt)
                .addUserMessageOfArrayOfContentParts(contentParts)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        return completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElse("");
    }

    private List<ChatCompletionContentPart> buildContentParts(List<UploadedFileReference> uploadedFiles) {
        List<ChatCompletionContentPart> parts = new ArrayList<>();
        String fileList = uploadedFiles.stream()
                .map(UploadedFileReference::getOriginalFileName)
                .collect(Collectors.joining(", "));
        parts.add(ChatCompletionContentPart.ofText(
                ChatCompletionContentPartText.builder()
                        .text("Analyze the attached files and return rename suggestions as JSON. "
                                + "Use these exact keys (original filenames): " + fileList)
                        .build()));

        for (UploadedFileReference uploadedFile : uploadedFiles) {
            if (uploadedFile.getInlineImageDataUrl() != null) {
                parts.add(ChatCompletionContentPart.ofImageUrl(
                        ChatCompletionContentPartImage.builder()
                                .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder()
                                        .url(uploadedFile.getInlineImageDataUrl())
                                        .build())
                                .build()));
            } else {
                parts.add(ChatCompletionContentPart.ofFile(
                        ChatCompletionContentPart.File.builder()
                                .file(ChatCompletionContentPart.File.FileObject.builder()
                                        .fileId(uploadedFile.getFileId())
                                        .build())
                                .build()));
            }
        }
        return parts;
    }
}