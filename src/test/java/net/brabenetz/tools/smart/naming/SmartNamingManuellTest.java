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
                .properties("spring.main.web-application-type=none")
                .run()) {

            SmartNamingService smartNamingService = context.getBean(SmartNamingService.class);
            List<File> files = getFiles("testfile A (2).jpg", "testfile A (1).jpg");
            // - testfile A (2).jpg -> 2026-04-23_Stromnetz-Graz_Smart-Meter-Anpassung_(2).jpg
            // - testfile A (1).jpg -> 2026-04-23_Stromnetz-Graz_Smart-Meter-Anpassung_(1).jpg
            // List<File> files = getFiles("testfile B.jpg"); // 2022-02-01_Billa_Einkauf_6,72EUR.jpg
            // List<File> files = getFiles("testfile C.jpg"); // 2022-02-01_Lidl_Einkauf-Rechnung_5,60EUR.jpg
            // List<File> files = getFiles("testfile D.jpg"); // 2022-02-02_Buchmesser_Rechnung_22,00EUR.jpg
            // List<File> files = getFiles("testfile E.jpg"); // 2022-02-03_Paracelsus-Apotheke_Apotheken-Rechnung_28,40EUR.jpg
            // List<File> files = getFiles("testfile F.jpg"); // 2022-02-04_Interspar_Einkauf_5,71EUR.jpg
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