package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix) {  //0代表永不过期
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {  //默认0代表永不过期
        return expireSeconds;
    }

    //返回对应类的前缀值。如userKey类前缀就是UserKey + 对应前缀(id/name)
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }


}
