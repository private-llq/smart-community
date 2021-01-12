package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @program: wechat
 * @description: 微信登录工具类
 * @author: Hu
 * @create: 2020-12-19 15:41
 **/
public class WeCharUtil {

    private final static String clientId="wxe84d22f50370bbda";
    private final static String clientSecret="650dae9d01862cfb1385f2fc55038cd2";


    /**
     * @Description: 通过code获取access_token和openid
     * @author: Hu
     * @since: 2021/1/11 16:33
     * @Param:
     * @return:
     */
    public static JSONObject getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + clientId + "&secret=" + clientSecret + "&code=" + code
                + "&grant_type=authorization_code";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String body = responseEntity.getBody();
        // 返回结果转换为json对象
        JSONObject jObject = JSONObject.parseObject(body);
        String accessToken = jObject.getString("access_token");
        String openid = jObject.getString("openid");
        return jObject;
    }

    /**
     * @Description: 获取用户信息
     * @author: Hu
     * @since: 2021/1/11 16:36
     * @Param:
     * @return:
     */
    public static JSONObject getUserInfo(String accessToken, String openid) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        String body = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        // 返回结果转换为json对象
        JSONObject jObject = JSONObject.parseObject(body);

        return jObject;
    }

    public static String getClientId() {
        return clientId;
    }

    public static String getClientSecret() {
        return clientSecret;
    }
}
