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

    /**
     * Imports a generated {@code .reg} file via {@code reg import} (requires Administrator on Windows).
     *
     * <p>Example input: {@code SmartNaming-Install.reg}
     * <br>On success, registry entries appear under {@code HKEY_CLASSES_ROOT\*\shell\}.
     *
     * @param registryFile {@code .reg} file to import
     * @throws SmartNamingException if the file is missing, access is denied, or import fails
     * @throws IOException if process streams cannot be read
     * @throws InterruptedException if the import process is interrupted
     */
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
            // reg import reports success on stderr on some Windows versions
            errorLines.forEach(LOG::info);
        }
    }
}