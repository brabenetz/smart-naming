package net.brabenetz.tools.smart.naming.core;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import net.brabenetz.tools.smart.naming.config.LlmAuthConfig;
import net.brabenetz.tools.smart.naming.config.LlmModelConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class OpenAiClientFactory {

    public OpenAIClient createClient(LlmModelConfig modelConfig) {
        String apiKey = "not-needed";
        LlmAuthConfig auth = modelConfig.getAuth();
        if (auth != null && StringUtils.isNotBlank(auth.getApiToken())) {
            apiKey = auth.getApiToken();
        }
        return OpenAIOkHttpClient.builder()
                .baseUrl(modelConfig.getUrl())
                .apiKey(apiKey)
                .build();
    }
}