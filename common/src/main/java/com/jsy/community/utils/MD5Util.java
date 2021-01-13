package com.jsy.community.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
* @Description: MD5工具类引入(from im组 @author liujinrong)
 * @Author: chq459799974
 * @Date: 2021/1/13
**/
public class MD5Util {
    
    public static final long MD5KEY = 1608187456577L;
    
    private static String getMd5Str(String str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(str.getBytes("UTF-8"));
            byte[] digest = md5.digest();
            String hexStr = byteArray2HexStr(digest);
            return hexStr.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byteArray2HexStr(byte[] bytes){
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            if (Integer.toHexString(0xFF & b).length() == 1) {
                buffer.append("0").append(Integer.toHexString(0xFF & b));
            } else {
                buffer.append(Integer.toHexString(0xFF & b));
            }
        }
        return buffer.toString();
    }
    
    //获取MD5签名
    public static String getSign(String str, Long time){
        return getMd5Str(str + (time ^ MD5KEY));
    }
    
    public static void main(String[] args) {
        String str = "PpXc749d8JzqMVCAqDFZGLdNfaQFbwjqgE9Z//y3wms=";
        Long time = System.currentTimeMillis();
        System.out.println(time);
        System.out.println(getMd5Str(str + (System.currentTimeMillis() ^ MD5KEY)));
        System.out.println(getSign(str,time));
    }
}
