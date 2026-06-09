package net.brabenetz.tools.smart.naming;

import net.brabenetz.tools.smart.naming.core.SmartNamingService;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manual test runner for local LM Studio / Open-AI-compatible endpoints.
 * Run main() from the IDE with the project root as working directory.
 */
public class SmartNamingManuellTest {

    private static final String EXAMPLE_FILES_DIR = "src/test/data/example-files";

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(SmartNamingCommandLineApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .profiles("manual")
                .properties(
                    // "spring.config.location=file:./src/main/resources/application.yml,classpath:/application-manual.yml",
                    "spring.main.web-application-type=none")
                .run()) {

            SmartNamingService smartNamingService = context.getBean(SmartNamingService.class);
            List<File> files = getFiles("testfile A (1).jpg", "testfile A (2).jpg");
            Map<String, String> suggestions = smartNamingService.run(files);

            System.out.println("=== Smart-Naming suggestions ===");
            suggestions.forEach((original, suggested) -> System.out.println(original + " -> " + suggested));
        }
    }

    private static List<File> getFiles(String... filenames) {
        return Arrays.stream(filenames)
                .map(filename -> new File(EXAMPLE_FILES_DIR, filename))
                .peek(file -> {
                    if (!file.isFile()) {
                        throw new IllegalArgumentException("Test file not found: " + file.getAbsolutePath());
                    }
                })
                .collect(Collectors.toList());
    }
}