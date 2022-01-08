package com.jsy.community.utils;

import java.nio.charset.StandardCharsets;
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
    
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /**
     * 客户端对用户密码进行二次加密，修改密码也必须调用此方法
     * @param password 源密码
     * @return 新密码
     */
    public static String getPassword(String password) {
        return getMd5Str("--zhsj--" + MD5Util.getMd5Str(password).substring(0, 10));
    }


    public static String getMd5Str(String str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md5.digest();
            String hexStr = byteArray2HexStr(digest);
            return hexStr.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
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
    
    //im组接口用户相关签名(同步实名状态)
    public static String signStr(Map param){
        if (param == null){
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
        return buffer.toString().substring(0,buffer.toString().lastIndexOf("&"));
    }
    
    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }
    
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));
        
        return resultSb.toString();
    }
    
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
    
}
