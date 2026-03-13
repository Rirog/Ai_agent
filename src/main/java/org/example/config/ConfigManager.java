package org.example.config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigManager {
    private static final ValuesConfig factory = ConfigFactory.create(ValuesConfig.class);

    public static String getBaseUrl() {
        return factory.baseUrl();
    }

    public static String getAiModel() {
        return factory.aiModel();
    }

    public static String getAiModelFast() {
        return factory.aiModelFast();
    }

    public static String getAiModelNew() {
        return  factory.aiModelNew();
    }

}
