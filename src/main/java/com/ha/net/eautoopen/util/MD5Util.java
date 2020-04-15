package com.ha.net.eautoopen.util;

import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    /**
     * 比较两个参数，使用明文密钥加密后的对比结果
     * @param request64MD5  请求的加密数据
     * @param key  明文密钥
     * @return
     */
    public static Boolean compare64MD5(String request64MD5,String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return StringUtils.hasText(request64MD5) ? request64MD5.equals(encoderMD5(key)) : false;
    }


    /**利用MD5进行加密
     * @param str  待加密的字符串
     * @return  加密后的字符串
     * */
    public static String encoderMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String newstr = null;
        if(StringUtils.hasText(str)){
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        }
        return newstr;
    }


    public static void main(String[] args) {
        try {
            System.out.println(encoderMD5("ICBC/icbc2020-prd"));
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
