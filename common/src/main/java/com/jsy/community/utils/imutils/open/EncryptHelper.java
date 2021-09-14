package com.jsy.community.utils.imutils.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author lxjr
 * @date 2021/8/16 15:14
 */
public class EncryptHelper {
    public static final String SECRET_KEY = "I|e7=N&?MUP?AnSwa0XNfXn^NewMsK:z";
    public static final String SECRET_IV = "363}&ODSGrEuC9p6";
    public static final String OPEN_ID = "open_7dcad41c19c24e7da0a61ab465c58bc0";

    public static final String DATA = "data";
    public static final String TIME = "time";
    public static final String SIGN = "sign";

    public static final String HEAD_ONLY_REQ = "onlyReq";
    public static final String HEAD_OPEN_ID = "openId";
    public static final String HEAD_DEVICE = "device";

    public static final String SECURITY_SECRET = "security_secret";

    /**
     * 接口请求加密
     *
     * @param jsonData  post请求中的body json参数
     * @param randomStr 随机字符串，和请求头保持一致
     * @return 加密后的参数，可以直接发起请求
     */
    public static String doPost(String jsonData, String randomStr) {
        return encrypt(jsonData, randomStr);
    }

    /**
     * 接口请求加密
     *
     * @param jsonData  post请求中的body json参数
     * @param randomStr 请求头 随机字符串，和请求头保持一致
     * @param device    请求头 设备类型 mobile、computer
     * @return 加密后的参数，可以直接发起请求
     */
    public static String doPost(String jsonData, String randomStr, String device) {
        return encrypt(jsonData, randomStr, device);
    }

    /**
     * 接口返回解密
     *
     * @param jsonBodyData response（必须是响应成功才可以，否则解密失败）
     * @return 解密后的data中的数据
     * @throws SignException 签名验证失败时抛出异常
     */
    public static Object resolvePost(String jsonBodyData) throws SignException {
        if (jsonBodyData == null) {
            return null;
        }
        JSONObject jb = checkSign(jsonBodyData);
        return decrypt(jb);
    }

    private static Object decrypt(JSONObject jb) {
        if (jb == null) {
            return null;
        }
        return decrypt((String) jb.get(DATA));
    }

    private static Object decrypt(String data) {
        if (data == null) {
            return null;
        }
        return AESUtil.decrypt(data, SECRET_KEY, SECRET_IV);
    }


    private static JSONObject checkSign(String jsonBodyData) throws SignException {
        JSONObject jb = JSON.parseObject(jsonBodyData);
        String sign = (String) jb.get(SIGN);
        if (!checkSign(jsonBodyData, sign)) {
            throw new SignException("返回结果签名错误！");
        }
        return jb;
    }

    public static boolean checkSign(String response, String targetSign) {
        JSONObject jp = JSON.parseObject(response);
        jp.put(SECURITY_SECRET, SECRET_KEY);
        jp.remove(SIGN);
        return checkSign(jp, targetSign);
    }

    public static boolean checkSign(Map map, String targetSign) {
        if (targetSign == null || targetSign.length() == 0) {
            return map == null || map.size() == 0;
        }
        return targetSign.equals(MD5Util.getMd5Str(MD5Util.signStr(map)));
    }

    private static String encrypt(String jsonData, String randomStr) {
        JSONObject jb = new JSONObject();
        jb.put(DATA, encrypt(jsonData));
        jb.put(TIME, System.currentTimeMillis());

        jb.put(HEAD_ONLY_REQ, randomStr);
        jb.put(HEAD_OPEN_ID, OPEN_ID);
        sign(jb);
        jb.remove(HEAD_ONLY_REQ);
        jb.remove(HEAD_OPEN_ID);
        return jb.toString();
    }

    private static String encrypt(String jsonData, String randomStr, String device) {
        JSONObject jb = new JSONObject();
        jb.put(DATA, encrypt(jsonData));
        jb.put(TIME, System.currentTimeMillis());

        jb.put(HEAD_ONLY_REQ, randomStr);
        jb.put(HEAD_OPEN_ID, OPEN_ID);
        jb.put(HEAD_DEVICE, device);
        sign(jb);
        jb.remove(HEAD_ONLY_REQ);
        jb.remove(HEAD_OPEN_ID);
        jb.remove(HEAD_DEVICE);
        return jb.toString();
    }

    private static String encrypt(String data) {
        if (data == null) {
            return null;
        }
        return AESUtil.encrypt(data, SECRET_KEY, SECRET_IV);
    }

    public static void sign(Map<String, Object> json) {
        json.put(SECURITY_SECRET, SECRET_KEY);
        String signStr = MD5Util.signStr(json);
        json.put(SIGN, MD5Util.getMd5Str(signStr));
        json.remove(SECURITY_SECRET);
    }

    /**
     * 客户端对用户密码进行二次加密，修改密码也必须调用此方法
     * @param password 源密码
     * @return 新密码
     */
    public static String getPassword(String password) {
        return MD5Util.getMd5Str("--zhsj--" + MD5Util.getMd5Str(password).substring(0, 10));
    }

}
