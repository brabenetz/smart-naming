package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.config.WindowsRegistryConfigs;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GenerateWindowsRegistryEntries {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateWindowsRegistryEntries.class);

    @Resource
    private WindowsRegistryConfigs windowsRegistryConfigs;

    public File generateRegistry() throws IOException {
        File targetFolder = windowsRegistryConfigs.getTargetFolder();
        File commandFile = createCommandFile(targetFolder);
        File registryFile = createRegistryFile(targetFolder, commandFile);

        LOG.info("Created: {}", commandFile);
        LOG.info("Created: {}", registryFile);
        return registryFile;
    }

    @SuppressWarnings("squid:S1192")
    private File createRegistryFile(File targetFolder, File commandFile) throws IOException {
        Collection<String> lines = new ArrayList<>();
        lines.add("Windows Registry Editor Version 5.00");
        lines.add(System.lineSeparator());

        addCommandToRegistry(lines, "Run Smart-Naming", commandFile,
                "-run", "--files", "%*");

        File registryFile = new File(targetFolder, "SmartNaming-Install.reg");
        FileUtils.writeLines(registryFile, StandardCharsets.ISO_8859_1.name(), lines);
        return registryFile;
    }

    private void addCommandToRegistry(Collection<String> lines, String commandName, File commandFile, String... args) throws IOException {
        String commandCanonicalPath = commandFile.getCanonicalPath();
        String commandLine = buildRegistryCommandLine(commandCanonicalPath, args);

        lines.add(String.format("[HKEY_CLASSES_ROOT\\*\\shell\\%s]", commandName));
        lines.add(String.format("@=\"%s\"", commandName));
        lines.add(System.lineSeparator());

        lines.add(String.format("[HKEY_CLASSES_ROOT\\*\\shell\\%s\\command]", commandName));
        lines.add(String.format("@=\"%s\"", StringEscapeUtils.escapeJava(commandLine)));
        lines.add(System.lineSeparator());
    }

    private String buildRegistryCommandLine(String commandCanonicalPath, String... args) {
        if (args.length > 0 && "%*".equals(args[args.length - 1])) {
            String[] quotedArgs = Arrays.copyOf(args, args.length - 1);
            String quotedPart = Stream.concat(Arrays.stream(new String[] {commandCanonicalPath}), Arrays.stream(quotedArgs))
                    .map(arg -> "\"" + arg + "\"")
                    .collect(Collectors.joining(" "));
            return quotedPart + " %*";
        }
        return Stream.concat(Arrays.stream(new String[] {commandCanonicalPath}), Arrays.stream(args))
                .map(arg -> "\"" + arg + "\"")
                .collect(Collectors.joining(" "));
    }

    private File createCommandFile(File targetFolder) throws IOException {
        File commandFile = new File(targetFolder, "SmartNaming-Registry.cmd");
        File installationFolder = windowsRegistryConfigs.getInstallationFolder();
        String installationFolderCanonicalPath = installationFolder.getCanonicalPath();
        String installationDrive = installationFolderCanonicalPath.substring(0, installationFolderCanonicalPath.indexOf(':') + 1);
        Validate.isTrue(new File(installationFolder, "smartnaming.cmd").exists(),
                "The Script 'smartnaming.cmd' cannot be found in %s",
                installationFolderCanonicalPath);

        Collection<String> lines = new ArrayList<>();
        lines.add("@echo off");
        lines.add("REM Wrapper for Smart-Naming call from Registry, mainly to set the right working directory");
        lines.add(StringUtils.EMPTY);
        lines.add(installationDrive);
        lines.add(String.format("cd %s", installationFolderCanonicalPath));
        lines.add("call smartnaming %*");
        lines.add("pause");

        FileUtils.writeLines(commandFile, StandardCharsets.ISO_8859_1.name(), lines);
        return commandFile;
    }
}