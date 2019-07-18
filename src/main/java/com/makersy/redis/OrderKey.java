package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public class OrderKey extends BasePrefix{

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");  //miaosha order uid gid

}
