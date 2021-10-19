package com.jsy.community.utils;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.UpdateBasic;
import com.jsy.community.utils.imutils.open.EncryptHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 聊天服务调用工具类
 * @Date: 2021/10/19 10:29
 * @Version: 1.0
 **/
@Component
public class CallUtil {

//    @Value("${imUrl}")
    public static String imUrl = "https://im.zhsj.co:8090";

    public static void sign(Map<String, Object> json) {
        json.put(EncryptHelper.SECURITY_SECRET, EncryptHelper.SECRET_KEY);
        String signStr = MD5Util.signStr(json);
        json.put(EncryptHelper.SIGN, MD5Util.getMd5Str(signStr));
        json.remove(EncryptHelper.SECURITY_SECRET);
    }

    private static String encrypt(String jsonData, String randomStr, String device) {
        JSONObject jb = new JSONObject();
        jb.put(EncryptHelper.DATA, encrypt(jsonData));
        jb.put(EncryptHelper.TIME, System.currentTimeMillis());

        jb.put(EncryptHelper.HEAD_ONLY_REQ, randomStr);
        jb.put(EncryptHelper.HEAD_OPEN_ID, EncryptHelper.OPEN_ID);
        jb.put(EncryptHelper.HEAD_DEVICE, device);
        sign(jb);
        jb.remove(EncryptHelper.HEAD_ONLY_REQ);
        jb.remove(EncryptHelper.HEAD_OPEN_ID);
        jb.remove(EncryptHelper.HEAD_DEVICE);
        return jb.toString();
    }

    private static String encrypt(String data) {
        if (data == null) {
            return null;
        }
        return AESUtil.encrypt(data, EncryptHelper.SECRET_KEY, EncryptHelper.SECRET_IV);
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
     * 同步聊天信息
     * @param imId
     * @param nickName
     * @param image
     */
    public static void updateUserInfo(String imId, String nickName, String image){
        UpdateBasic updateBasic = new UpdateBasic(imId, nickName, image, image);
        String str = String.valueOf(SnowFlake.nextId());
        HttpResponse response = HttpUtil.createPost(imUrl + "/zhsj/im/user/basicInfo/sync/updateBasic")
                .header(EncryptHelper.HEAD_OPEN_ID, EncryptHelper.OPEN_ID)
                .header(EncryptHelper.HEAD_ONLY_REQ, str)
                .header(EncryptHelper.HEAD_DEVICE, "mobile")
                .body(doPost(JSON.toJSONString(updateBasic), str, "mobile"))
                .execute();

        String body = response.body();
        System.out.println(body);
        System.out.println(resolvePost(body));
    }

    /**
     * 接口返回解密
     *
     * @param jsonBodyData response（必须是响应成功才可以，否则解密失败）
     * @return 解密后的data中的数据
     * @throws JSYException 签名验证失败时抛出异常
     */
    public static Object resolvePost(String jsonBodyData) throws JSYException {
        if (jsonBodyData == null) {
            return null;
        }
        JSONObject jb = checkSign(jsonBodyData);
        return decrypt(jb);
    }

    private static JSONObject checkSign(String jsonBodyData) throws JSYException {
        JSONObject jb = JSON.parseObject(jsonBodyData);
        String sign = (String) jb.get(EncryptHelper.SIGN);
        if (!checkSign(jsonBodyData, sign)) {
            throw new JSYException("返回结果签名错误！");
        }
        return jb;
    }

    public static boolean checkSign(String response, String targetSign) {
        JSONObject jp = JSON.parseObject(response);
        jp.put(EncryptHelper.SECURITY_SECRET, EncryptHelper.SECRET_KEY);
        jp.remove(EncryptHelper.SIGN);
        return checkSign(jp, targetSign);
    }

    public static boolean checkSign(Map map, String targetSign) {
        if (targetSign == null || targetSign.length() == 0) {
            return map == null || map.size() == 0;
        }
        return targetSign.equals(MD5Util.getMd5Str(MD5Util.signStr(map)));
    }

    private static Object decrypt(JSONObject jb) {
        if (jb == null) {
            return null;
        }
        return decrypt((String) jb.get(EncryptHelper.DATA));
    }

    private static Object decrypt(String data) {
        if (data == null) {
            return null;
        }
        return AESUtil.decrypt(data, EncryptHelper.SECRET_KEY, EncryptHelper.SECRET_IV);
    }
}
