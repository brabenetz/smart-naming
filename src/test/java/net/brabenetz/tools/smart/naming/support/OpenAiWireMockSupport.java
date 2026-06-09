package net.brabenetz.tools.smart.naming.support;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public final class OpenAiWireMockSupport {

    public static final int WIREMOCK_PORT = 18089;

    private OpenAiWireMockSupport() {
    }

    public static WireMockRule createRule() {
        return new WireMockRule(wireMockConfig().port(WIREMOCK_PORT));
    }

    public static void stubFileUpload(String fileId) {
        String body = "{"
                + "\"id\":\"" + fileId + "\","
                + "\"object\":\"file\","
                + "\"bytes\":123,"
                + "\"created_at\":1234567890,"
                + "\"filename\":\"upload.jpg\","
                + "\"purpose\":\"vision\""
                + "}";
        stubFor(post(urlEqualTo("/v1/files"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    public static void stubChatCompletion(String content) {
        String escapedContent = content.replace("\\", "\\\\").replace("\"", "\\\"");
        String body = "{"
                + "\"id\":\"chatcmpl-test\","
                + "\"object\":\"chat.completion\","
                + "\"created\":1234567890,"
                + "\"model\":\"test-model\","
                + "\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\""
                + escapedContent
                + "\"},\"finish_reason\":\"stop\"}],"
                + "\"usage\":{\"prompt_tokens\":10,\"completion_tokens\":20,\"total_tokens\":30}"
                + "}";
        stubFor(post(urlEqualTo("/v1/chat/completions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody(body)));
    }

    public static File createMinimalJpeg(File targetFile) throws IOException {
        org.apache.commons.io.FileUtils.forceMkdirParent(targetFile);
        byte[] minimalJpeg = {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00,
                (byte) 0xFF, (byte) 0xD9
        };
        org.apache.commons.io.FileUtils.writeByteArrayToFile(targetFile, minimalJpeg);
        return targetFile;
    }
}