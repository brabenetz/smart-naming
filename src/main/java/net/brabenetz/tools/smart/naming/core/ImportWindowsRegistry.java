package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.config.WindowsRegistryConfigs;
import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class ImportWindowsRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ImportWindowsRegistry.class);

    @Resource
    private WindowsRegistryConfigs windowsRegistryEntriesConfigs;

    public void importRegistry(File registryFile) throws IOException, InterruptedException {
        if (!registryFile.exists()) {
            throw new SmartNamingException(String.format("Not found: '%s'", registryFile.getAbsolutePath()));
        }
        LOG.info("Import: {}", registryFile);

        Process importer = new ProcessBuilder("reg", "import", registryFile.getCanonicalPath()).start();

        int exitCode = importer.waitFor();

        IOUtils.readLines(importer.getInputStream()).forEach(LOG::info);
        List<String> errorLines = IOUtils.readLines(importer.getErrorStream());

        if (exitCode != 0) {
            errorLines.forEach(LOG::error);
            if (errorLines.size() == 1 && errorLines.get(0).contains("Error accessing the registry")) {
                throw new SmartNamingException("You must run this script as Administrator");
            } else {
                throw new SmartNamingException(String.format("could not import: '%s'", registryFile.getAbsolutePath()));
            }
        } else {
            errorLines.forEach(LOG::info);
        }
    }
}