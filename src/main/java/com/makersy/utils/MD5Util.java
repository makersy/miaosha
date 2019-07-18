package com.makersy.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * Created by makersy on 2019
 */

//用户输入密码将进行两次md5加密，一次是在客户端进行，一次在服务端进行。
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";


    //将用户输入密码转换成表单密码。即第一次md5
    public static String inputPass2FormPass(String inputPass) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    //第二次md5
    public static String formPass2DBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPass2DBPass(String input, String saltDB) {
        String formPass = inputPass2FormPass(input);
        String dbpass = formPass2DBPass(formPass, saltDB);
        return dbpass;
    }

    @Test
    public void test(){
        String password = "111111";
        String salt = "helloworld";
        System.out.println(MD5Util.inputPass2DBPass(password, salt));
//        System.out.println(MD5Util.inputPass2FormPass(password)); //d018506bc314a32b93eb214102399a63
    }
}
