package net.brabenetz.tools.smart.naming.config;

import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmartNamingConfigsTest.TestConfiguration.class)
public class SmartNamingConfigsTest {

    @Autowired
    private SmartNamingConfigs smartNamingConfigs;

    @Test
    public void resolveActiveModelUsesDefaultFromYaml() {
        LlmModelConfig model = smartNamingConfigs.resolveActiveModel();

        assertThat(smartNamingConfigs.getUsedModel()).isEqualTo("local-qwen");
        assertThat(model.getUrl()).isEqualTo("http://localhost:1234/v1");
        assertThat(model.getModel()).isEqualTo("qwen3.5_4B-test");
        assertThat(model.getAuth()).isNull();
    }

    @Test
    public void resolveModelWithValidKey() {
        LlmModelConfig model = smartNamingConfigs.resolveModel("open-ai");

        assertThat(model.getUrl()).isEqualTo("https://api.openai.com/v1");
        assertThat(model.getModel()).isEqualTo("gpt-4-test");
        assertThat(model.getAuth()).isNotNull();
        assertThat(model.getAuth().getApiToken()).isEqualTo("test-token");
    }

    @Test
    public void resolveModelWithUnknownKeyThrows() {
        assertThatThrownBy(() -> smartNamingConfigs.resolveModel("unknown-model"))
                .isInstanceOf(SmartNamingException.class)
                .hasMessageContaining("Unknown model key 'unknown-model'");
    }

    @Test
    public void resolveModelWithBlankKeyThrows() {
        assertThatThrownBy(() -> smartNamingConfigs.resolveModel("  "))
                .isInstanceOf(SmartNamingException.class)
                .hasMessageContaining("No model key configured");
    }

    @Test
    public void systemPromptIsLoaded() {
        assertThat(smartNamingConfigs.getSystemPrompt())
                .contains("Test system prompt line 1")
                .contains("Test system prompt line 2");
    }

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.context.properties.EnableConfigurationProperties(SmartNamingConfigs.class)
    static class TestConfiguration {
    }
}