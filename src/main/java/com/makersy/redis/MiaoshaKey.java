package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public class MiaoshaKey extends BasePrefix{

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");

    private MiaoshaKey(String prefix) {
        super(prefix);
    }
}
