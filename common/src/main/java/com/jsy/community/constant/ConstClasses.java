package com.jsy.community.constant;

import com.jsy.community.utils.AESOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author chq459799974
 * @description 常量类(For 静态注入)
 * @since 2021-01-12 10:47
 **/
public interface ConstClasses {
	
	/**
	* @Description: 支付宝数据资料常量类
	 * @Author: chq459799974
	 * @Date: 2021/1/12
	**/
	@Component
	class AliPayDataEntity{
		public static String appid;//appid
		public static String privateKey;//秘钥
		
		public static String certPath;//应用公钥证书路径
		public static String alipayPublicCertPath;//支付宝公钥证书路径
		public static String rootCertPath;//支付宝根证书路径
		
		public static String sellerId;//商户ID
		public static String sellerEmail;//商户账号(邮箱)
		public static String sellerPID;//商户账号(PID)
		
		@Value("${alipay.appid}")
		public void setAppid(String appid) {
			AliPayDataEntity.appid = AESOperator.decrypt(appid);
		}
		@Value("${alipay.sellerId}")
		public void setSellerId(String sellerId) {
			AliPayDataEntity.sellerId = AESOperator.decrypt(sellerId);
		}
		@Value("${alipay.sellerEmail}")
		public void setSellerEmail(String sellerEmail) {
			AliPayDataEntity.sellerEmail = AESOperator.decrypt(sellerEmail);
		}
		@Value("${alipay.sellerPID}")
		public void setSellerPID(String sellerPID) {
			AliPayDataEntity.sellerPID = AESOperator.decrypt(sellerPID);
		}
		
		@Value("${alipay.app-private-key}")
		public void setPrivateKey(String privateKey) {
			AliPayDataEntity.privateKey = AESOperator.decrypt(privateKey);
		}
		@Value("${alipay.cert-path.app-public-cert}")
		public void setCertPath(String certPath) {
			AliPayDataEntity.certPath = AESOperator.decrypt(certPath);
		}
		@Value("${alipay.cert-path.alipay-public-cert}")
		public void setAlipayPublicCertPath(String alipayPublicCertPath) {
			AliPayDataEntity.alipayPublicCertPath = AESOperator.decrypt(alipayPublicCertPath);
		}
		@Value("${alipay.cert-path.root-cert}")
		public void setRootCertPath(String rootCertPath) {
			AliPayDataEntity.rootCertPath = AESOperator.decrypt(rootCertPath);
		}
	}
	
	/**
	 * @Description: 阿里云数据常量类
	 * @Author: chq459799974
	 * @Date: 2021/5/31
	 **/
	@Component
	class AliYunDataEntity{
		
		public static String appCode;//公司阿里云appCode
		
		public static String URL_ID_CARD_OCR;//身份证文字识别三方接口
		
		@Value("${aliyun.appCode}")
		public void setAppCode(String appCode) {
			AliYunDataEntity.appCode = AESOperator.decrypt(appCode);
		}
		
		@Value("${aliyun.url.id-card-ocr}")
		public void setUrlIdCardOcr(String urlIdCardOcr) {
			AliYunDataEntity.URL_ID_CARD_OCR = urlIdCardOcr;
		}
	}
	
}
