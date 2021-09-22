package com.jsy.community.utils.signature;

import com.alibaba.fastjson.JSON;
import lombok.Data;


import java.util.Map;

/**
 * 电子签章 1.0 加密解密工具
 *
 * @author
 * @version 1.0
 * @date 2021/9/15 14:50
 */
public class ZhsjUtil {
    private static final String key = "?b@R~@Js6yH`aFal=LAHg?l~K|ExYJd;";
    private static final String iv = "1E}@+?f-voEy;_?r";

    /**
     * post 请求加密
     *
     * @param jsonStr json字符串
     * @return 加密后的body参数
     */
    public static String postEncrypt(String jsonStr) {
        final String data = AESUtil.encrypt(jsonStr, key, iv);
        final Request ok = Request.ok(data);
        Map map = JSON.parseObject(JSON.toJSONString(ok), Map.class);
        map.put("secret", "巴拉啦小魔仙");
        map.put("time", ok.getTime());
        String signStr = MD5Util.signStr(map);
        String md5Str = MD5Util.getMd5Str(signStr);
        ok.setSign(md5Str);
        return JSON.toJSONString(ok);
    }

    /**
     * post 請求解密
     * @param data 接口返回數據data
     * @return
     */
    public static String postDecrypt(String data) {
        return AESUtil.decrypt(data, key, iv);
    }
}

@Data
class Request<T> {
    /**
     * 业务状态码
     */
    private Integer code;

    /**
     * 响应描述信息
     */
    private String message;

    /**
     * 返回数据
     * 这个 字段：data不能改，只能叫data，一旦改了可能影响到全局解密
     */
    private T data;

    /**
     * 时间戳
     */
    private Long time;
    /**
     * 签名
     */
    private String sign;

    public static Request ok(String data) {
        Request request = new Request();
        request.setCode(0);
        request.setMessage("ok");
        request.setData(data);
        request.setTime(System.currentTimeMillis());
        return request;
    }
}
