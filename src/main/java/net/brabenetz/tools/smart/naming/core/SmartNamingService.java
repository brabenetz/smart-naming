package net.brabenetz.tools.smart.naming.core;

import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SmartNamingService {

    private static final Logger LOG = LoggerFactory.getLogger(SmartNamingService.class);

    @Resource
    private SmartNamingConfigs smartNamingConfigs;

    public void run() {
        LOG.info("Running Smart-Naming with property: {}", smartNamingConfigs.getSomeProperty());
    }
}