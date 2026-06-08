package net.brabenetz.tools.smart.naming;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
    public void runMainShort() {
        SmartNamingCommandLineApplication.main("-run");
    }

    @Test
    public void runMainLong() {
        SmartNamingCommandLineApplication.main("--run");
    }

    @Test
    public void runGenerateWindowsRegistryEntriesShort() {
        SmartNamingCommandLineApplication.main("-gwre");
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Registry.cmd")).exists();
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Install.reg")).exists();
    }

    @Test
    public void runGenerateWindowsRegistryEntriesLong() {
        SmartNamingCommandLineApplication.main("--generateWindowsRegistryEntries");
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Registry.cmd")).exists();
        assertThat(new File(TEST_RESULT_DIRECTORY, "SmartNaming-Install.reg")).exists();
    }
}