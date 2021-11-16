package com.jsy.community.untils.cebbank;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/10 14:37
 * @Version: 1.0
 **/
public class VerifyUtil {
    public static boolean verify(String publicKey, String sign, String content , String charset){
        byte[] publicKey2 = Base64.decodeBase64(publicKey);//对提供的公钥做base64解码
        byte[] signtrue= Base64.decodeBase64(Base64.decodeBase64(sign));//签名需要做两次base64解码
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey2);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));
            return signature.verify(signtrue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
