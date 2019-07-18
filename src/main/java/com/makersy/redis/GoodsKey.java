package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public class GoodsKey extends BasePrefix {

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //设置60s的缓存时间
    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0, "gs"); //永久不失效

}
