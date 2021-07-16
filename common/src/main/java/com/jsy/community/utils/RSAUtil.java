package com.jsy.community.utils;

import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: RSA非对称加密工具类
 * @Author: liujinrong
 * @Date: 2021/7/15
 **/
@Slf4j
public class RSAUtil {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA"; // ALGORITHM ['ælgərɪð(ə)m] 算法的意思
    
    //通用公钥
    public static final String COMMON_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsDwbbbFwUhYFuLWXBoudI3/x0+sqEQglktQgBwuzP5E/p+0m1O13oK5jcD71WISYyXZHq/pMBh1RyUkHUqWP5d6fN4TOHtvJwhMXtNDhK3TNqDQKhPK+8aRbomZBxnZ7/eJso/CdBH2p9BHjYzI3ALMvHzH7HjKX+PoVub/lw1FlDzrT8amxnKDL3TwSBIrCEf31cSh3+UnsQl8+4+lC17bMFa6M8B2J3DEygfYk+LqEfrdTHoVMA3bBY1MTSlnM4HdD3LcnL5qWVDruiLx4ncLMyV2vyB9m/+HSYg3bQsOs2ZmywPmypGiG74TvnTa0HjBpoaVpruQ70fuTSVpp1QIDAQAB";
    //通用私钥
    public static final String COMMON_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCwPBttsXBSFgW4tZcGi50jf/HT6yoRCCWS1CAHC7M/kT+n7SbU7XegrmNwPvVYhJjJdker+kwGHVHJSQdSpY/l3p83hM4e28nCExe00OErdM2oNAqE8r7xpFuiZkHGdnv94myj8J0Efan0EeNjMjcAsy8fMfseMpf4+hW5v+XDUWUPOtPxqbGcoMvdPBIEisIR/fVxKHf5SexCXz7j6ULXtswVrozwHYncMTKB9iT4uoR+t1MehUwDdsFjUxNKWczgd0PctycvmpZUOu6IvHidwszJXa/IH2b/4dJiDdtCw6zZmbLA+bKkaIbvhO+dNrQeMGmhpWmu5DvR+5NJWmnVAgMBAAECggEAPb/h0G6Sr4Bd4tlloHp8xbqHzjwg0jTrjWXcDvvvg39uMXWr3IGUH+3ak7LLnodfTX+vHzglOSM+y+tVsISoRd/aI8bZvgA+0kyESuvBacuUX7JUWT8A3oQ7Q0zXESXkzhFvBYZCTheF3CoEZXZrxUgDY8fMeEk77JYb1KjDZcriOZNXFuJvW3LEBCm1GWGYId1uYbgcHXIqpm4MlW+9vaRg1h3DERL9Bcj3i8wE9vecsMhQMHsV80CLuYml9Xkt+Ze11fSZBfjdjdejiPP6Cgmo0LF/8fFPkqP6UQmKuJ35A8pRuWZwsWjdNOjEWpBqveu45xWrbq9J0co5v1GBwQKBgQDw5dDsJWxX9bHNoW5Ax4oo66a4uj6rwDPOTgZFFPK7jY6lc1OX3GgCf7/800jdQ6YaUG4voFDHEYp7X3+592QR+m6NtqsBS4wtzD29y8JUMoP7FfBSRuPWVKmyceSfE+A1K11dfRffk63Px0ZPWHNjIEhftXRepYncyNo1563fxQKBgQC7SILnYgBBI1vw4O/LbdMQBQujJ7qynsGiM3CFYXATwT4NWByUhdWXI3ljj30TxGnZOa3l3fpMN+VOV4MhgLju2j4Ype/MRh5bFBDuEHkCwwfv9vuZpTSi3+lUHMGRsHsIn4nUrtlRNfhH8Y/qwiEA/MObnJX5hLwxSwRFzs1y0QKBgQDZzY+l0vgwVw5ZcxlyLHd4JAfrgUjklLTCVL1KfHQyqvxMDweHiFnp2INHHekFVQK7S/kzymj+c5eXkHK1Oz4ZGyoVqxDV7O5jAgTYs3+SDNJTlDRiz4Fz6bw8gMlKIvhWWkziGBYbqTQ/Zlblqn05JJkLH86FPHp3SoowrrYAWQKBgAKp0VLBDgffHNucF0zpTusXZ0b2taz8nPxVhvAepp1hTiSacC2ciaEQnBVSYRD65hZ0v4hZU8npsZUfoXnEftzGtcNb+MEK7juVWXhJO69SXraG6KUSuGDolkTf89DClb1Vp3wi0GxbPCCpysYn5JhC//UikO9vd82rp2/mtP1BAoGBANHSL8SnFmVHycpa74Mubxk+Wbt4oh+jqBe47oZEnfPFPQ9vToJXxkwcNvv7L6SNw6Px5PlG7OiepET+1s2LRCFYL5nFGIaOEnfOY+lrQ7yKKNzmlDrkqC53/jGKR+yXVo9pjSiRY8gzRWycsuYrzkrS1KAnL9WgK+wsxgoFgAPy";

    public static Map<String, String> createKeys(int keySize) {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // map装载公钥和私钥
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        // 返回map
        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey  密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey  密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("没有这个算法：" + RSA_ALGORITHM);
            throw new JSYException(JSYError.INTERNAL);
        }
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        RSAPrivateKey key;
        try {
            key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (InvalidKeySpecException e) {
            log.error("非法私钥：" + privateKey);
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            log.error("解密字符串[" + data + "]时遇到异常", e);
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            //每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    //rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;  //最大块
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    //可以调用以下的doFinal（）方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        IOUtils.closeQuietly(out);
        return resultDatas;
    }


    // 简单测试____________
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Map<String, String> keyMap = RSAUtil.createKeys(2048);
        String publicKey = keyMap.get("publicKey");
        String privateKey = keyMap.get("privateKey");
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("公钥加密——私钥解密");
        String str = "11111111";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
        String encodedData = RSAUtil.publicEncrypt(str, RSAUtil.getPublicKey(publicKey));  //传入明文和公钥加密,得到密文
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = RSAUtil.privateDecrypt(encodedData, RSAUtil.getPrivateKey(privateKey)); //传入密文和私钥,得到明文
        System.out.println("解密后文字: \r\n" + decodedData);

        System.out.println((System.currentTimeMillis() - start)/1000+"."+(System.currentTimeMillis() - start)%1000);
    }

}
