package com.jsy.community.untils.cebbank;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/10 14:35
 * @Version: 1.0
 **/
public class SignUtil {
    public static String getSign(String privateKey,String content,String charset){
        String sign = "";
        try {
            byte[] keyBytes = Base64.decodeBase64(privateKey);//对私钥做base64解码
            byte[] contentBytes = content.getBytes(charset);//获取签名内容的字节数组
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(priKey);
            signature.update(contentBytes);//data为要生成签名的源数据字节数组
            byte[] encodeBase64 = Base64.encodeBase64(Base64.encodeBase64(signature.sign()));//进行两次base64编码
            sign = new String(encodeBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }
}
