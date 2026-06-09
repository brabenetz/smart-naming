package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileRenameServiceTest {

    private static final File TEST_DIR = new File("target/test-tmp/FileRenameServiceTest");

    private FileRenameService fileRenameService;

    @Before
    public void setUp() throws IOException {
        fileRenameService = new FileRenameService();
        if (TEST_DIR.exists()) {
            FileUtils.deleteDirectory(TEST_DIR);
        }
        FileUtils.forceMkdir(TEST_DIR);
    }

    @Test
    public void renameFileMovesToSuggestedName() throws IOException {
        File source = writeFile("original.jpg", "content");
        Map<String, String> suggestions = Collections.singletonMap(
                "original.jpg", "2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg");

        fileRenameService.renameFiles(Collections.singletonList(source), suggestions);

        File expected = new File(TEST_DIR, "2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg");
        assertThat(expected).exists();
        assertThat(source).doesNotExist();
    }

    @Test
    public void renameSkipsWhenNameUnchanged() throws IOException {
        File source = writeFile("2026-03-01_Test_Source.jpg", "content");
        Map<String, String> suggestions = Collections.singletonMap(
                "2026-03-01_Test_Source.jpg", "2026-03-01_Test_Source.jpg");

        fileRenameService.renameFiles(Collections.singletonList(source), suggestions);

        assertThat(source).exists();
    }

    @Test
    public void renameFailsWhenTargetExists() throws IOException {
        File source = writeFile("original.jpg", "content");
        writeFile("2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg", "other");
        Map<String, String> suggestions = Collections.singletonMap(
                "original.jpg", "2026-03-01_Hofer-Rechnung_Milch-Brot_12,34EUR.jpg");

        assertThatThrownBy(() -> fileRenameService.renameFiles(Collections.singletonList(source), suggestions))
                .isInstanceOf(SmartNamingException.class)
                .hasMessageContaining("Target file already exists");
        assertThat(source).exists();
    }

    @Test
    public void renameFailsWhenSuggestionMissing() throws IOException {
        File source = writeFile("original.jpg", "content");

        assertThatThrownBy(() -> fileRenameService.renameFiles(
                Collections.singletonList(source), new HashMap<String, String>()))
                .isInstanceOf(SmartNamingException.class)
                .hasMessageContaining("Missing rename suggestion");
    }

    private static File writeFile(String name, String content) throws IOException {
        File file = new File(TEST_DIR, name);
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        return file;
    }
}