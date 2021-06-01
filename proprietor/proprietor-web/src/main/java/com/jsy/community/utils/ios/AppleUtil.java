package com.jsy.community.utils.ios;

import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Base64;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Map;

/**
 * 苹果工具类
 *
 * @author :  
 * @version : 1.0.0
 * @date :   2020/7/13 17:10
 */
@Slf4j
public class AppleUtil {

    private static final String AUTH_TIME = "auth_time";

    private static final String APPLE_SERVER_ULR = "https://appleid.apple.com";

    /*public static void main(String[] args) {
        AppleTokenVo appleTokenVo = decodeIdentityToken("eyJraWQiOiJlWGF1bm1MIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLmNoYW5nZGFvLnR0c2Nob29sIiwiZXhwIjoxNTg5MjcwMzI3LCJpYXQiOjE1ODkyNjk3MjcsInN1YiI6IjAwMTk0MC43YTExNDFhYTAwMWM0NjllYTE1NjNjNmJhZTk5YzM3ZC4wMzA3IiwiY19oYXNoIjoienNIUW9xbTdjcDZOcmxrUHFhTmpGQSIsImVtYWlsIjoiYXEzMmsydnpjd0Bwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTU4OTI2OTcyNywibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.q5unOzswOjpRYmrVKVm3FRb_Th6kkhgEvoFfTEAIETwgTXZ7bYcQM8J8tCjkGGqtt2z74Z-wTW7Q3ia209xhmwrVDIup0jcQgNTvsCEMkfo9evPIDrNRNQw2Dzw2EBKma8004NL6THYlySoDnPRoW_VQCHP_m0HnjYuIc-wtREEClf-_tOFDPpTsvUFoETHNfhpsLhqj24-zm6MSOocYY3WbUaYJQVEFCz-x6AGko1XkMtms_-JU1xakNtjMZTIVj2XyUI5MO7_eo-D9i_c7Hj-OE9HNBEvFnPxOesDzXvEoYdb7uByXEfa-H1syJMecBMRa3tL76W_CYKsONRkU9Q");
        System.out.println(appleTokenVo);
    }*/

    /**
     * 获取publicKey 的算法id
     *
     * @param identityToken 苹果token的第一部分
     * @return String
     */
    public static String getKid(String identityToken) {
        String kid = null;
        try {
            String str1 = new String(Base64.decodeBase64(identityToken), StandardCharsets.UTF_8);
            Map<String, Object> data = JSON.parseObject(str1, Map.class);
            kid = (String) data.get("kid");
        } catch (Exception e) {
            log.error("get kid fail,e={}", e);
        }
        return kid;
    }

    /**
     * 解密个人信息
     *
     * @param identityToken APP获取的identityToken的第二部分
     * @return 解密参数：失败返回null  sub就是用户id,用户昵称需要前端传过来
     */
    public static AppleTokenVo getAppleUserInfo(String identityToken) {
        AppleTokenVo appleTokenVo = null;
        try {
            String str2 = new String(Base64Utils.decodeFromString(identityToken), StandardCharsets.UTF_8);
            appleTokenVo = JSON.parseObject(str2, AppleTokenVo.class);
        } catch (Exception e) {
            log.info("get apple user information fail,e={} ", e);
        }
        return appleTokenVo;
    }

    /**
     * 验证
     *
     * @param identityToken APP获取的identityToken
     * @param aud           您在您的Apple Developer帐户中的client_id
     * @param sub           用户的唯一标识符对应APP获取到的：user
     * @return true/false
     */
    public static boolean verifyIdentityToken(PublicKey publicKey, String identityToken, String aud, String sub) {
        try {
            JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
            jwtParser.requireIssuer(APPLE_SERVER_ULR);
            jwtParser.requireAudience(aud);
            jwtParser.requireSubject(sub);
            Jws<Claims> claim = jwtParser.parseClaimsJws(identityToken);
            if (claim != null && claim.getBody().containsKey(AUTH_TIME)) {
                return true;
            }
        } catch (ExpiredJwtException e1) {
            log.error("apple token verify fail,identityToken is expired!");
        } catch (Exception e2) {
            log.error("apple token verify fail,error={}", e2);
            log.error("apple token verify fail,error={}", e2.getMessage());
        }
        return false;
    }

}
