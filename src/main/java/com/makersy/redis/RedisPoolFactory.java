package com.makersy.redis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by makersy on 2019
 */

@Service
public class RedisPoolFactory {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.valueOf(redisConfig.getPool().get("maxActive")));
        poolConfig.setMaxIdle(Integer.valueOf(redisConfig.getPool().get("maxIdle")));

        //注意单位是ms，要转换单位
        poolConfig.setMaxWaitMillis(Integer.valueOf(redisConfig.getPool().get("maxWait")) * 1000);
        poolConfig.setMinIdle(Integer.valueOf(redisConfig.getPool().get("minIdle")));

        return new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout()*1000, redisConfig.getPassword(), 0);
    }


}
