package com.jsy.community.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @Description: 支付宝请求参数构建签名 (三方登录授权、支付等) 安卓官方demo
 * @Author: chq459799974
 * @Date: 2020/3/8
 **/
public class SignUtils {

	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static String getAlgorithms(boolean rsa2) {
		return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
	}
	
	public static String sign(String content, String privateKey, boolean rsa2) {
		try {
			Base64.Encoder encoder = Base64.getEncoder();
			Base64.Decoder decoder = Base64.getDecoder();
//			encoder.encode(privateKey.getBytes());
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
				decoder.decode(privateKey.getBytes()));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(getAlgorithms(rsa2));

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

//			return Base64.encode(signed);
			return encoder.encodeToString(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
