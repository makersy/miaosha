package com.makersy.redis;

/**
 * Created by makersy on 2019
 */

public class MiaoshaKey extends BasePrefix{

    /**
     * 商品卖完
     */
    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");

    /**
     * 验证码
     */
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "mv");

    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
