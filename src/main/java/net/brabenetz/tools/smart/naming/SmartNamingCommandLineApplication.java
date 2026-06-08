package net.brabenetz.tools.smart.naming;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SmartNamingCommandLineApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SmartNamingCommandLineApplication.class);

    public static void main(final String... args) {
        new SpringApplicationBuilder(SmartNamingCommandLineApplication.class)
                .bannerMode(Mode.OFF)
                .parent(new SpringApplicationBuilder(AppPrepare.class)
                        .banner(new SmartNamingBanner())
                        .run(args))
                .run(args);
    }

    public static class AppPrepare implements InitializingBean {
        @Override
        public void afterPropertiesSet() throws Exception {
            LOG.info("Current Java-Version: {}; OS: {}; Timezone: {}; Lang: {}",
                    SystemUtils.JAVA_VERSION, SystemUtils.OS_NAME, SystemUtils.USER_TIMEZONE, SystemUtils.USER_LANGUAGE);
        }
    }
}