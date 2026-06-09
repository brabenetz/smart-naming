package net.brabenetz.tools.smart.naming;

import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import net.brabenetz.tools.smart.naming.core.GenerateWindowsRegistryEntries;
import net.brabenetz.tools.smart.naming.core.ImportWindowsRegistry;
import net.brabenetz.tools.smart.naming.core.SmartNamingService;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"squid:S1148", "squid:S2221"})
@Profile({"!test", "!manual"})
public class SmartNamingRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SmartNamingRunner.class);

    @Resource
    private SmartNamingService smartNamingService;
    @Resource
    private SmartNamingConfigs smartNamingConfigs;
    @Resource
    private GenerateWindowsRegistryEntries generateWindowsRegistryEntries;
    @Resource
    private ImportWindowsRegistry importWindowsRegistry;

    @Override
    public void run(final String... args) {
        Option helpOption = new Option("h", "help", false, "print this help-screen.");
        Option runOption = new Option("r", "run", false, "run the main application logic.");
        Option filesOption = Option.builder("f")
                .longOpt("files")
                .hasArgs()
                .desc("selected files to process (required with --run)")
                .build();
        Option generateRegistryOption = new Option("gwre", "generateWindowsRegistryEntries", false,
                "generate Windows Registry files for File Explorer context menu");
        Option importRegistryOption = new Option("i", "install", false,
                "install Windows Registry entries (requires Administrator)");

        Options options = new Options();
        options.addOption(helpOption);
        options.addOption(runOption);
        options.addOption(filesOption);
        options.addOption(generateRegistryOption);
        options.addOption(importRegistryOption);

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args, true);

            if (cmd.hasOption("run")) {
                LOG.info("run --{}", runOption.getLongOpt());
                String[] filePaths = cmd.getOptionValues("files");
                if (filePaths == null || filePaths.length == 0) {
                    throw new SmartNamingException("Option --files is required with --run (at least one file)");
                }
                List<File> files = Arrays.stream(filePaths)
                        .map(File::new)
                        .collect(Collectors.toList());
                smartNamingService.run(files);
            } else if (cmd.hasOption("gwre")) {
                LOG.info("run --{}", generateRegistryOption.getLongOpt());
                generateWindowsRegistryEntries.generateRegistry();
            } else if (cmd.hasOption("i")) {
                LOG.info("run --{}", importRegistryOption.getLongOpt());
                File registryFile = generateWindowsRegistryEntries.generateRegistry();
                importWindowsRegistry.importRegistry(registryFile);
            } else {
                LOG.info("run --help");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(120,
                        "\n  smartnaming -run --files <file1> <file2> ... --smartnaming.used-model=<key>\n\n",
                        "smartnaming",
                        options,
                        "\n© Brabenetz Harald");
            }
        } catch (Exception e) {
            SmartNamingException appException = getAppExceptionIfPossible(e);
            if (appException != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(appException.getMessage(), e);
                } else {
                    LOG.error(appException.getMessage());
                }
            } else {
                LOG.error("Unknown Error Smart-Naming: {}", e.getMessage(), e);
            }
        }
    }

    private static SmartNamingException getAppExceptionIfPossible(final Throwable e) {
        if (e instanceof SmartNamingException) {
            return (SmartNamingException) e;
        }
        if (e == null) {
            return null;
        }
        return getAppExceptionIfPossible(e.getCause());
    }
}