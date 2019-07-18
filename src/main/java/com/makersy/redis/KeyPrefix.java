package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
