package com.jsy.community.utils.ios;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: ios登录工具类
 * @author: Hu
 * @create: 2021-05-31 15:22
 **/
public  class IOSUtil {

    public static PublicKey getPublicKey(String kid) {
        String body=null;
        try {
            String url = "https://appleid.apple.com/auth/keys";
            HttpUriRequest httpGet = new HttpGet(url);
            httpGet.setHeader("Content-type", "application/problem+json;charset=UTF-8");
            httpGet.setHeader("Accept", "application/json;charset=UTF-8");
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse execute = client.execute(httpGet);
            HttpEntity entity = execute.getEntity();
            body = EntityUtils.toString(entity, "UTF-8");
            JSONObject jObject = JSONObject.parseObject(body);
            List<AppleKeyVo> appleKeyVos = JSONUtil.toList(JSONUtil.parseArray(jObject.get("keys")),AppleKeyVo.class);
            for (AppleKeyVo appleKeyVo : appleKeyVos) {
                if (kid.equals(appleKeyVo.getKid())){
                    BigInteger modulus = new BigInteger(1, Base64.decodeBase64(appleKeyVo.getN()));
                    BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(appleKeyVo.getE()));
                    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    return kf.generatePublic(spec);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
