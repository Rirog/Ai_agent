package org.example.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:values.properties"})
public interface ValuesService {

    @Config.Key("base.url")
    String getBaseUrl();

    @Config.Key("ai.model")
    String getAiModel();

}
