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
		
		@Value("${alipay.appid}")
		public void setAppid(String appid) {
			this.appid = AESOperator.decrypt(appid);
		}
		@Value("${alipay.app-private-key}")
		public void setPrivateKey(String privateKey) {
			this.privateKey = AESOperator.decrypt(privateKey);
		}
		@Value("${alipay.cert-path.app-public-cert}")
		public void setCertPath(String certPath) {
			this.certPath = AESOperator.decrypt(certPath);
		}
		@Value("${alipay.cert-path.alipay-public-cert}")
		public void setAlipayPublicCertPath(String alipayPublicCertPath) {
			this.alipayPublicCertPath = AESOperator.decrypt(alipayPublicCertPath);
		}
		@Value("${alipay.cert-path.root-cert}")
		public void setRootCertPath(String rootCertPath) {
			this.rootCertPath = AESOperator.decrypt(rootCertPath);
		}
	}
	
}
