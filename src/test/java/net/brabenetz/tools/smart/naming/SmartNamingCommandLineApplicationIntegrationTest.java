package net.brabenetz.tools.smart.naming;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class SmartNamingCommandLineApplicationIntegrationTest {

    private static final File TEST_RESULT_DIRECTORY = new File("./target/tests/SmartNamingCommandLineApplicationIntegrationTest");

    @Before
    public void cleanTestDirectory() throws IOException {
        if (TEST_RESULT_DIRECTORY.exists()) {
            FileUtils.deleteDirectory(TEST_RESULT_DIRECTORY);
        }
    }

    @Test
    public void runDefaultHelp() {
        SmartNamingCommandLineApplication.main();
        assertThat(TEST_RESULT_DIRECTORY).doesNotExist();
    }

    @Test
    public void runMainShort() throws IOException {
        File testFile = createTempFile("single");
        SmartNamingCommandLineApplication.main("-r", "-f", testFile.getAbsolutePath());
    }

    @Test
    public void runMainLong() throws IOException {
        File testFile = createTempFile("single-long");
        SmartNamingCommandLineApplication.main("--run", "--files", testFile.getAbsolutePath());
    }

    @Test
    public void runMainMultipleFiles() throws IOException {
        File file1 = createTempFile("multi-1");
        File file2 = createTempFile("multi-2");
        File file3 = createTempFile("multi-3");
        SmartNamingCommandLineApplication.main("--run", "--files",
                file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath());
    }

    @Test
    public void runGenerateWindowsRegistryEntriesShort() throws IOException {
        SmartNamingCommandLineApplication.main("-gwre");
        File registryFile = new File(TEST_RESULT_DIRECTORY, "SmartNaming-Install.reg");
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Registry.cmd")).exists();
        assertThat(registryFile).exists();
        assertRegistryContent(registryFile);
    }

    @Test
    public void runGenerateWindowsRegistryEntriesLong() throws IOException {
        SmartNamingCommandLineApplication.main("--generateWindowsRegistryEntries");
        File registryFile = new File(TEST_RESULT_DIRECTORY, "SmartNaming-Install.reg");
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Registry.cmd")).exists();
        assertThat(registryFile).exists();
        assertRegistryContent(registryFile);
    }

    private static File createTempFile(String name) throws IOException {
        if (!TEST_RESULT_DIRECTORY.exists()) {
            FileUtils.forceMkdir(TEST_RESULT_DIRECTORY);
        }
        File file = new File(TEST_RESULT_DIRECTORY, name + ".txt");
        FileUtils.writeStringToFile(file, "test", StandardCharsets.UTF_8);
        return file;
    }

    private static void assertRegistryContent(File registryFile) throws IOException {
        String content = new String(Files.readAllBytes(registryFile.toPath()), StandardCharsets.ISO_8859_1);
        assertThat(content).contains("[HKEY_CLASSES_ROOT\\*\\shell\\Run Smart-Naming]");
        assertThat(content).contains("\\\"-run\\\" \\\"--files\\\" %*");
        assertThat(content).doesNotContain("Directory\\shell");
    }
}