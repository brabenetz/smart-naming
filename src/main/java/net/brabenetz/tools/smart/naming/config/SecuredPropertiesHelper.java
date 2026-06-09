package net.brabenetz.tools.smart.naming.config;

import net.brabenetz.lib.securedproperties.SecuredProperties;
import net.brabenetz.lib.securedproperties.SecuredPropertiesConfig;

import java.io.File;

public class SecuredPropertiesHelper {
    private static final SecuredPropertiesConfig config = new SecuredPropertiesConfig().initDefault();

    private static File[] propertyFiles = new File[] {new File("./config/application.properties"), new File("./application.properties")};

    private SecuredPropertiesHelper() {
        // hide constructor
    }

    public static void encryptProperties(String... keys) {
        SecuredProperties.encryptNonEncryptedValues(config, propertyFiles, keys);
    }

    public static String decrypt(String value) {
        if (SecuredProperties.isEncryptedValue(value)) {
            return SecuredProperties.decrypt(config, value);
        }
        return value;
    }

    public static String getEncryptedValue(String value) {
        if (!SecuredProperties.isEncryptedValue(value)) {
            return SecuredProperties.encrypt(config, value);
        }
        return value;
    }

}
