package net.brabenetz.tools.smart.naming.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "smartnaming")
public class SmartNamingConfigs {

    private String someProperty = "default-value";

    public String getSomeProperty() {
        return someProperty;
    }

    public void setSomeProperty(String someProperty) {
        this.someProperty = someProperty;
    }
}