package com.jsy.community.utils;


import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSON;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * @Description: im组接口通用加密工具类引入(除推送)(from im组 @author liujinrong)
 * @Author: chq459799974
 * @Date: 2021/1/25
 **/
public class AESUtil {
    
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String openId = "3fc89344d8c54bb5b1327ef4035a4827";
    private static final String key = "a296af040b694a36b37e8fee9200b129";
    private static final String iv = "8sd1a3ml7oxb731z";
    
    //获取im组通用接口传递对象
    public static OpenParam returnOpenParam(Object obj) {
        OpenParam openParam = new OpenParam();
        openParam.setErr_code(0);
        openParam.setErr_msg("ok");
        openParam.setOpen_id(openId);
        openParam.setTimestamp(System.currentTimeMillis());
        openParam.setData(encrypt(JSON.toJSONString(obj), key, iv));
        String signStr = MD5Util.signStr(openParam, key);
        String md5Str = MD5Util.getMd5Str(signStr);
        openParam.setSignature(md5Str);
        return openParam;
    }

//    private static final String HMACKEY = "34aecdaf351f3513d11ed61e6b51f81f5867bc13bb1c9da9cfc975f7ed8229a5";

    public static String AES_cbc_encrypt(String srcData, String key, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] encData = cipher.doFinal(srcData.getBytes());
        return Base64Encoder.encode(encData);
    }

    public static String AES_cbc_decrypt(String encData, String key, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] decbbdt = cipher.doFinal(Base64Decoder.decode(encData));
        return new String(decbbdt);
    }
    
    //加密
    public static String encrypt(String data, String key, String iv) {
        try {
            return AES_cbc_encrypt(data, key, iv);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    //解密
    public static String decrypt(String data){
        return decrypt(data,key,iv);
    }
    
    //解密
    public static String decrypt(String data, String key, String iv) {
        try {
            return AES_cbc_decrypt(data, key, iv);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

}