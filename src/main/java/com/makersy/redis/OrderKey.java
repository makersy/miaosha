package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super(prefix);
    }

    /**
     * miaosha order uid gid
     */
    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");

}
