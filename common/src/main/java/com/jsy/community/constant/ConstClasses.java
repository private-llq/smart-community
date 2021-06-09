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
		
		public static String smsAccessKeyId;//阿里云短信子账号accessKeyId
		public static String smsSecret;//阿里云短信子账号secret
		
		public static String appCode;//公司阿里云appCode
		
		public static String URL_ID_CARD_OCR;//身份证文字识别三方接口
		
		public static String URL_WEATHER_NOW;//天气实况三方接口
		public static String URL_WEATHER_DAYS;//15天天气预报三方接口
		public static String URL_WEATHER_HOURS;//24小时天气预报三方接口
		public static String URL_WEATHER_AIR;//空气质量
		public static String URL_WEATHER_LIVING;//生活指数
		
		@Value("${aliyun.sms.smsAccessKeyId}")
		public void setSmsAccessKeyId(String smsAccessKeyId) {
			AliYunDataEntity.smsAccessKeyId = AESOperator.decrypt(smsAccessKeyId);
		}
		@Value("${aliyun.sms.smsSecret}")
		public void setSmsSecret(String smsSecret) {
			AliYunDataEntity.smsSecret = AESOperator.decrypt(smsSecret);
		}
		
		@Value("${aliyun.appCode}")
		public void setAppCode(String appCode) {
			AliYunDataEntity.appCode = AESOperator.decrypt(appCode);
		}
		
		@Value("${aliyun.url.id-card-ocr}")
		public void setUrlIdCardOcr(String urlIdCardOcr) {
			AliYunDataEntity.URL_ID_CARD_OCR = urlIdCardOcr;
		}
		
		@Value("${aliyun.url.weather.now}")
		public void setUrlWeatherNow(String urlWeatherNow) {
			AliYunDataEntity.URL_WEATHER_NOW = urlWeatherNow;
		}
		@Value("${aliyun.url.weather.days}")
		public void setUrlWeatherDays(String urlWeatherDays) {
			AliYunDataEntity.URL_WEATHER_DAYS = urlWeatherDays;
		}
		@Value("${aliyun.url.weather.hours}")
		public void setUrlWeatherHours(String urlWeatherHours) {
			AliYunDataEntity.URL_WEATHER_HOURS = urlWeatherHours;
		}
		@Value("${aliyun.url.weather.air}")
		public void setUrlWeatherAir(String urlWeatherAir) {
			AliYunDataEntity.URL_WEATHER_AIR = urlWeatherAir;
		}
		@Value("${aliyun.url.weather.living}")
		public void setUrlWeatherLiving(String urlWeatherLiving) {
			AliYunDataEntity.URL_WEATHER_LIVING = urlWeatherLiving;
		}
	}
	
}
