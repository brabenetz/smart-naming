package net.brabenetz.tools.smart.naming.config;

import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = SmartNamingConfigsTest.TestConfiguration.class)
public class SmartNamingConfigsTest {

    @Autowired
    private SmartNamingConfigs smartNamingConfigs;

    @Test
    public void resolveActiveModelUsesDefaultFromYaml() {
        LlmModelConfig model = smartNamingConfigs.resolveActiveModel();

        assertThat(smartNamingConfigs.getUsedModel()).isEqualTo("wiremock-test");
        assertThat(model.getUrl()).isEqualTo("http://localhost:18089/v1");
        assertThat(model.getModel()).isEqualTo("test-model");
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
                .contains("Return ONLY JSON");
    }

    @Test
    public void maxRetriesDefaultFromYaml() {
        assertThat(smartNamingConfigs.getMaxRetries()).isEqualTo(3);
    }

    @Test
    public void targetFilenamePatternIsLoadedOrHasDefault() {
        assertThat(smartNamingConfigs.getTargetFilenamePattern()).isNotBlank();
        assertThat(smartNamingConfigs.getCompiledTargetFilenamePattern()
                .matcher("2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg")
                .matches()).isTrue();
        assertThat(smartNamingConfigs.getCompiledTargetFilenamePattern()
                .matcher("2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR_(1).jpg")
                .matches()).isTrue();
        assertThat(smartNamingConfigs.getCompiledTargetFilenamePattern()
                .matcher("2026-05-01_Anwaltsschreiben-XY_Erwachsenenvertretung_(2).jpg")
                .matches()).isTrue();
        assertThat(smartNamingConfigs.getCompiledTargetFilenamePattern()
                .matcher("invalid-name.jpg")
                .matches()).isFalse();
    }

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.context.properties.EnableConfigurationProperties(SmartNamingConfigs.class)
    static class TestConfiguration {
    }
}