package org.example.config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigManager {
    private static final ValuesConfig factory = ConfigFactory.create(ValuesConfig.class);

    public static String getBaseUrl() {
        return factory.baseUrl();
    }

    public static String getAiModelFast() {
        return factory.aiModelFast();
    }

    public static String getEmailHost() {
        return factory.emailHost();
    }

    public static String getEmailProtocol() {
        return factory.emailProtocol();
    }

    public  static String getEmailFolder(){
        return factory.emailFolder();
    }

    public  static String getEmailSmtpHost(){
        return factory.emailSmtpHost();
    }

    public  static String getEmailSmtpPort(){
        return factory.emailSmtpPort();
    }

}
