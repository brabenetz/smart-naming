package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class SmartNamingService {

    private static final Logger LOG = LoggerFactory.getLogger(SmartNamingService.class);

    @Resource
    private SmartNamingConfigs smartNamingConfigs;

    public void run(List<File> files) {
        LOG.info("Running Smart-Naming for {} file(s) with property: {}", files.size(), smartNamingConfigs.getSomeProperty());
        files.forEach(file -> LOG.info("  file: {}", file.getAbsolutePath()));
    }
}