package net.brabenetz.tools.smart.naming;

import net.brabenetz.tools.smart.naming.config.SecuredPropertiesHelper;
import net.brabenetz.tools.smart.naming.config.SmartNamingConfigs;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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

  @EnableConfigurationProperties(SmartNamingConfigs.class)
  public static class AppPrepare implements InitializingBean {

    @Autowired
    private SmartNamingConfigs smartNamingConfigs;

    @Override
    public void afterPropertiesSet() throws Exception {
      LOG.info("Current Java-Version: {}; OS: {}; Timezone: {}; Lang: {}",
          SystemUtils.JAVA_VERSION, SystemUtils.OS_NAME, SystemUtils.USER_TIMEZONE, SystemUtils.USER_LANGUAGE);

      // auto-encrypt tokens in application.properties
      String[] tokenKeys = smartNamingConfigs.getModels().keySet().stream()
          .map((key) -> "smartnaming.models." + key + ".auth.api-token")
          .toArray(String[]::new);
      SecuredPropertiesHelper.encryptProperties(tokenKeys);

    }
  }
}