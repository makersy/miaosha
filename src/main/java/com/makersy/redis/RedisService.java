package com.makersy.redis;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by makersy on 2019
 */

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     * @param prefix 前缀，用来区分是哪一部分的对象
     * @param key
     * @param clazz 获取对象类型
     * @param <T> 获取
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            return stringToBean(str, clazz);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = beanToString(value);
            //判断是否设置了有效期，如果没有设置就是永久有效
            if (prefix.expireSeconds() <= 0) {
                //未设置
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, prefix.expireSeconds(), str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断对象是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<?> clazz) {
        if (str == null || StringUtils.isBlank(str) || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T)str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T)Long.valueOf(str);
        } else if (clazz==double.class || clazz == Double.class) {
            return (T) Double.valueOf(str);
        } else {
            return (T) JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }


    //将某个对象转换为string字符串，需注意如果不是可以通过fastjson处理的bean，需要特殊处理
    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return ""+value;
        } else if (clazz==String.class) {
            return (String)value;
        } else if (clazz == long.class || clazz == Long.class) {
            return ""+value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    //释放jedis连接到连接池
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
