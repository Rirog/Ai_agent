package org.example.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:values.properties"})
public interface ValuesConfig extends Config {

    @Key("base.url")
    String baseUrl();

    @Key("ai.model")
    String aiModel();

    @Key("ai.model.fast")
    String aiModelFast();

    @Key("ai.model.new")
    String aiModelNew();
}
