package com.vpbanks.timeline.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisHostPortProperties {
    private String host;
    private Integer port;
    private String password;
}
