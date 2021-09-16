package com.jsy.community.utils.signature;

import cn.hutool.core.util.ObjectUtil;
import com.jsy.community.exception.JSYException;
import org.apache.commons.codec.binary.Hex;


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

    private static String byteArray2HexStr(byte[] bytes) {
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
        return buffer.toString().substring(0, buffer.toString().lastIndexOf("&"));
    }


    /**
     * 生成含有随机盐的密码
     */
    public static String generate(String password) {
        Optional.ofNullable(password).orElseThrow(() -> new JSYException(400, "参数错误"));
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }


    /**
     * 校验密码是否正确
     */
    public static boolean verify(String password, String md5) {
        if (ObjectUtil.isNull(password) || ObjectUtil.isNull(md5)) {
            return false;
        }
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return Objects.equals(md5Hex(password + salt), new String(cs1));
    }


    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    public static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }


}
