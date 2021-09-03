package com.jsy.community.utils.imutils.open;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class MD5Util {

    public static final int NumberCtt_42 = 42;
    public static final char AND = '&';
    public static final char EQ = '=';
    private static final String SIGN = "sign";

    public static String getMd5Str(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(str.getBytes("UTF-8"));
            byte[] digest = md5.digest();
            String hexStr = byteArray2HexStr(digest);
            return hexStr.toLowerCase();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteArray2HexStr(byte[] bytes) {
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


    /**
     * 获取待签名字符串
     *
     * @param param 参数
     * @return 获取待签名字符串
     */
    public static String signStr(Map param) {
        if (param == null) {
            return null;
        }
        /* 获取字段， 先排序 */
        List<String> field = new ArrayList<>();
        Iterator<String> iterator = param.keySet().iterator();
        String k;
        while (iterator.hasNext()) {
            k = iterator.next();
            if (SIGN.equals(k)) {
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
            if (v == null) {
                continue;
            } else {
                ve = v.getClass().isArray() ? ((String[]) v)[0] : v.toString();
            }
            buffer.append(k).append(EQ).append(ve).append(AND);
        }
        return buffer.toString().substring(0, buffer.toString().lastIndexOf(AND));
    }
}
