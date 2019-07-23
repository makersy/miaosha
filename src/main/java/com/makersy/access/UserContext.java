package com.makersy.access;

import com.makersy.domain.MiaoshaUser;

/**
 * @author yhl
 * @date 2019/7/23
 */


public class UserContext {

    /**
     * 使用 ThreadLocal 来保存 user
     * 读取请求以及处理请求都是同一个线程，所以将user对象放入线程内存中，保证user对象是线程安全的
     */
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }
}
