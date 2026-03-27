package org.example.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:values.properties"})
public interface ValuesConfig extends Config {

    @Key("base.url")
    String baseUrl();

    @Key("ai.model.fast")
    String aiModelFast();

    @Key("email.host")
    String emailHost();

    @Key("email.protocol")
    String emailProtocol();


    @Key("email.folder")
    String emailFolder();

    @Key("email.smtp.host")
    String emailSmtpHost();

    @Key("email.smtp.port")
    String emailSmtpPort();
}
