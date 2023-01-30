package com.srlab.basic.serverside.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("external")
@Getter
@Setter
public class YamlConfig {
    private String count;
    private String key;
    private String salt;
    private String temp;
    private String path;
}
