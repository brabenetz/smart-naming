package net.brabenetz.tools.smart.naming;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import net.brabenetz.tools.smart.naming.support.OpenAiWireMockSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class SmartNamingCommandLineApplicationIntegrationTest {

    private static final File TEST_RESULT_DIRECTORY = new File("./target/tests/SmartNamingCommandLineApplicationIntegrationTest");

    @ClassRule
    public static WireMockRule wireMockRule = OpenAiWireMockSupport.createRule();

    @BeforeClass
    public static void stubOpenAiOnce() {
        OpenAiWireMockSupport.stubFileUpload("file-cli-test");
    }

    @Before
    public void setUp() throws IOException {
        if (TEST_RESULT_DIRECTORY.exists()) {
            FileUtils.deleteDirectory(TEST_RESULT_DIRECTORY);
        }
        wireMockRule.resetRequests();
        OpenAiWireMockSupport.stubFileUpload("file-cli-test");
    }

    @Test
    public void runDefaultHelp() {
      SmartNamingCommandLineApplication.main("--spring.profiles.active=test");
        assertThat(TEST_RESULT_DIRECTORY).doesNotExist();
    }

    @Test
    public void runMainShort() throws IOException {
        File testFile = createTempJpeg("single");
        stubSuggestion(testFile.getName(), "2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg");
        SmartNamingCommandLineApplication.main("-r", "-f", "--spring.profiles.active=test", testFile.getAbsolutePath());
    }

    @Test
    public void runMainLong() throws IOException {
        File testFile = createTempJpeg("single-long");
        stubSuggestion(testFile.getName(), "2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg");
        SmartNamingCommandLineApplication.main("--run", "--spring.profiles.active=test", "--files", testFile.getAbsolutePath());
    }

    @Test
    public void runMainMultipleFiles() throws IOException {
        File file1 = createTempJpeg("multi-1");
        File file2 = createTempJpeg("multi-2");
        File file3 = createTempJpeg("multi-3");
        OpenAiWireMockSupport.stubChatCompletion("{"
                + "\"" + file1.getName() + "\":\"2026-05-01_Anwaltsschreiben-XY_Erwachsenenvertretung_(1).jpg\","
                + "\"" + file2.getName() + "\":\"2026-05-01_Anwaltsschreiben-XY_Erwachsenenvertretung_(2).jpg\","
                + "\"" + file3.getName() + "\":\"2026-05-01_Anwaltsschreiben-XY_Erwachsenenvertretung_(3).jpg\""
                + "}");
        SmartNamingCommandLineApplication.main("--run", "--spring.profiles.active=test", "--files",
                file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath());
    }

    @Test
    public void runGenerateWindowsRegistryEntriesShort() throws IOException {
      SmartNamingCommandLineApplication.main("-gwre", "--spring.profiles.active=test");
        File registryFile = new File(TEST_RESULT_DIRECTORY, "SmartNaming-Install.reg");
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Registry.cmd")).exists();
        assertThat(registryFile).exists();
        assertRegistryContent(registryFile);
    }

    @Test
    public void runGenerateWindowsRegistryEntriesLong() throws IOException {
      SmartNamingCommandLineApplication.main("--generateWindowsRegistryEntries", "--spring.profiles.active=test");
        File registryFile = new File(TEST_RESULT_DIRECTORY, "SmartNaming-Install.reg");
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Registry.cmd")).exists();
        assertThat(registryFile).exists();
        assertRegistryContent(registryFile);
    }

    private static File createTempJpeg(String name) throws IOException {
        if (!TEST_RESULT_DIRECTORY.exists()) {
            FileUtils.forceMkdir(TEST_RESULT_DIRECTORY);
        }
        return OpenAiWireMockSupport.createMinimalJpeg(new File(TEST_RESULT_DIRECTORY, name + ".jpg"));
    }

    private static void stubSuggestion(String originalName, String suggestedName) {
        OpenAiWireMockSupport.stubChatCompletion("{\"" + originalName + "\":\"" + suggestedName + "\"}");
    }

    private static void assertRegistryContent(File registryFile) throws IOException {
        String content = new String(Files.readAllBytes(registryFile.toPath()), StandardCharsets.ISO_8859_1);
        assertThat(content).contains("[HKEY_CLASSES_ROOT\\*\\shell\\RunSmartNaming]");
        assertThat(content).contains("\"MultiSelectModel\"=\"Player\"");
        assertThat(content).contains("-run --files \\\"\\\\\\\"$files\\\\\\\"\\\" --si-timeout 400");
        assertThat(content).doesNotContain("Directory\\shell");
    }
}