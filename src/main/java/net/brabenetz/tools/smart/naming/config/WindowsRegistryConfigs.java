package net.brabenetz.tools.smart.naming.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConfigurationProperties(prefix = "windows-registry")
public class WindowsRegistryConfigs {

    private File installationFolder = new File("./");

    private File targetFolder = new File("./windows-registry");

    public File getInstallationFolder() {
        return installationFolder;
    }

    public void setInstallationFolder(File installationFolder) {
        this.installationFolder = installationFolder;
    }

    public File getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(File targetFolder) {
        this.targetFolder = targetFolder;
    }
}