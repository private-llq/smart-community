package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @program: wechat
 * @description: 微信登录工具类
 * @author: Hu
 * @create: 2020-12-19 15:41
 **/
public class WeCharUtil {

    private final static String clientId="wxe84d22f50370bbda";
    private final static String clientSecret="e03145398568e0e174e9bd635b889a54";

    @Resource
    private RestTemplate restTemplate;




    /**
     * @Description: 通过code获取access_token和openid
     * @author: Hu
     * @since: 2021/1/11 16:33
     * @Param:
     * @return:
     */
    public static JSONObject getAccessToken(String code) {
        String body=null;
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+clientId+"&secret="+clientSecret+"&code="+code+"&grant_type=authorization_code";
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "application/json;charset=UTF-8");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            org.apache.http.HttpEntity entity = response.getEntity();

            body = EntityUtils.toString(entity, "UTF-8");

            JSONObject jObject = JSONObject.parseObject(body);
            return jObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 获取用户信息
     * @author: Hu
     * @since: 2021/1/11 16:36
     * @Param:
     * @return:
     */
    public static JSONObject getUserInfo(String accessToken, String openid) {
        String body=null;
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-type", "application/json;charset=UTF-8");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            org.apache.http.HttpEntity entity = response.getEntity();

            body = EntityUtils.toString(entity, "UTF-8");

            JSONObject jObject = JSONObject.parseObject(body);
            return jObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getClientId() {
        return clientId;
    }

    public static String getClientSecret() {
        return clientSecret;
    }
}
