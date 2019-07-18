package com.makersy.redis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by makersy on 2019
 */

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
@NoArgsConstructor
public class RedisConfig {

    private String host;

    private int port;

    /**
     * 单位：秒
     */
    private int timeout;

    private String password;

    private HashMap<String, String> pool = new HashMap<>();

}
