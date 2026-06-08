package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.config.LlmModelConfig;
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
        LlmModelConfig activeModel = smartNamingConfigs.resolveActiveModel();
        LOG.info("Running Smart-Naming for {} file(s) with model key '{}': url={}, model={}",
                files.size(), smartNamingConfigs.getUsedModel(), activeModel.getUrl(), activeModel.getModel());
        LOG.info("System prompt length: {} characters", smartNamingConfigs.getSystemPrompt() != null
                ? smartNamingConfigs.getSystemPrompt().length() : 0);
        files.forEach(file -> LOG.info("  file: {}", file.getAbsolutePath()));
    }
}