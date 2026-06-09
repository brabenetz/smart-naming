package net.brabenetz.tools.smart.naming.core;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import net.brabenetz.tools.smart.naming.SmartNamingCommandLineApplication;
import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import net.brabenetz.tools.smart.naming.support.OpenAiWireMockSupport;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = SmartNamingCommandLineApplication.class)
public class SmartNamingServiceIntegrationTest {

    @ClassRule
    public static WireMockRule wireMockRule = OpenAiWireMockSupport.createRule();

    @Resource
    private SmartNamingService smartNamingService;

    @Resource
    private SmartNamingConfigs smartNamingConfigs;

    private File testImage;

    @Before
    public void setUp() throws IOException {
        smartNamingConfigs.setUsedModel("wiremock-test");
        smartNamingConfigs.setMaxRetries(3);

        File testTmpDir = new File("target/test-tmp");
        if (testTmpDir.exists()) {
            org.apache.commons.io.FileUtils.cleanDirectory(testTmpDir);
        }
        testImage = OpenAiWireMockSupport.createMinimalJpeg(new File(testTmpDir, "smart-naming-service-test.jpg"));

        wireMockRule.resetAll();
        OpenAiWireMockSupport.stubFileUpload("file-test-1");
    }

    @Test
    public void runReturnsValidSuggestions() {
        OpenAiWireMockSupport.stubChatCompletion(
                "{\"smart-naming-service-test.jpg\":\"2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg\"}");

        smartNamingService.run(Collections.singletonList(testImage));

        verify(postRequestedFor(urlEqualTo("/v1/files")));
        verify(postRequestedFor(urlEqualTo("/v1/chat/completions")));
        assertThat(testImage).doesNotExist();
        assertThat(new File(testImage.getParentFile(), "2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg")).exists();
    }

    @Test
    public void runRetriesOnInvalidResponse() {
        wireMockRule.stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/v1/chat/completions"))
                .inScenario("retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(invalidChatBody("not-json")))
                .willSetStateTo("second"));

        wireMockRule.stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/v1/chat/completions"))
                .inScenario("retry")
                .whenScenarioStateIs("second")
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(validChatBody("{\"smart-naming-service-test.jpg\":\"2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg\"}"))));

        smartNamingService.run(Collections.singletonList(testImage));

        verify(2, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    @Test
    public void runFailsAfterMaxRetries() {
        OpenAiWireMockSupport.stubChatCompletion("not-json");

        assertThatThrownBy(() -> smartNamingService.run(Collections.singletonList(testImage)))
                .isInstanceOf(SmartNamingException.class)
                .hasMessageContaining("Failed to obtain valid naming suggestions after 3 attempts");

        verify(3, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    private static String invalidChatBody(String content) {
        return validChatBody(content);
    }

    private static String validChatBody(String content) {
        String escapedContent = content.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{"
                + "\"id\":\"chatcmpl-test\","
                + "\"object\":\"chat.completion\","
                + "\"created\":1234567890,"
                + "\"model\":\"test-model\","
                + "\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\""
                + escapedContent
                + "\"},\"finish_reason\":\"stop\"}],"
                + "\"usage\":{\"prompt_tokens\":10,\"completion_tokens\":20,\"total_tokens\":30}"
                + "}";
    }
}