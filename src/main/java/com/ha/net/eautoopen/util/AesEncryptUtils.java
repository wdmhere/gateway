package com.ha.net.eautoopen.util;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
@Slf4j
public class AesEncryptUtils {

    //参数分别代表 算法名称/加密模式/数据填充方式
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 加密
     * @param content 加密的字符串
     * @param encryptKey key值
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) throws Exception {

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cut16(encryptKey).getBytes(), "AES"));
        byte[] b = cipher.doFinal(content.getBytes("utf-8"));
        // 采用base64算法进行转码,避免出现中文乱码
        return Base64.getEncoder().encodeToString(b);
    }

    /**
     * 解密
     * @param encryptStr 解密的字符串
     * @param decryptKey 解密的key值
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(cut16(decryptKey).getBytes(), "AES"));
            // 采用base64算法进行转码,避免出现中文乱码
            byte[] encryptBytes = Base64.getDecoder().decode(encryptStr);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            log.error("解密失败：",e);
            throw new Exception("解密失败！");
        }
    }


    private static String cut16(String orgkey)throws Exception{
        return orgkey.substring(0,16);
    }




    public static void main(String[] args) {
        try {
            String a = encrypt("我爱你中国！！","a1StXZNaMeTI77lyaHlaZw==".substring(0,16));
            System.out.println("a = "+ a);
            System.out.println(decrypt(a,"a1StXZNaMeTI77lyaHlaZw==".substring(0,16)));
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

}
