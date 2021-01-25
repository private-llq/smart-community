package com.jsy.community.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
* @Description: MD5工具类引入(from im组 @author liujinrong)
 * @Author: chq459799974
 * @Date: 2021/1/13
**/
public class MD5Util {
    
    public static final long MD5KEY = 1608187456577L;
    
    public static final String SIGN = "sign";
    public static final char AND = '&';
    public static final char EQ = '=';
    public static final int NumberCtt_42 = 42;
    
    public static String getMd5Str(String str){
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
    
    //im组接口通用签名(除推送)
    public static String signStr(OpenParam openParam, String secretKey){
        HashMap<String, String> param = new HashMap<>();
        param.put("open_id",openParam.getOpen_id());
        param.put("timestamp",openParam.getTimestamp()+"");
        param.put("data",openParam.getData());
        param.put("secretKey",secretKey);
        
        if (openParam == null){
            return null;
        }
        /* 获取字段， 先排序 */
        List<String> field = new ArrayList<>();
        Iterator<String> iterator = param.keySet().iterator();
        String k;
        while (iterator.hasNext()) {
            k = iterator.next();
            if (SIGN.equals(k)){
                continue;
            }
            field.add(k);
        }
        /* 排序 */
        Collections.sort(field);
        /* 生成待签名字符串*/
        StringBuffer buffer = new StringBuffer(param.size() * NumberCtt_42);
        Object v;
        String ve;
        for (int i = 0; i < field.size(); i++) {
            k = field.get(i);
            v = param.get(k);
            if (v == null){
                continue;
            }else {
                ve = v.getClass().isArray() ? ((String[]) v)[0] : v.toString();
            }
            buffer.append(k).append(EQ).append(ve).append(AND);
        }
        return buffer.toString();
    }
}
