package com.jsy.community.constant;

public interface Const {
	String version = "1.0";
	
	String group = "dev";
	String group_property = "property"; //物业端
	String group_proprietor = "proprietor"; //业主端
	String group_lease = "lease"; //房屋租售端
	String group_payment = "payment"; //移动支付端
	String group_facility = "facility"; //设备端

	interface ThirdPlatformConsts {
		int ALIPAY = 1;//支付宝
		int WECHAT = 2;//微信
		int QQ = 3;//QQ
	}
	
	/**
	 * 已申请短信签名
	 */
	interface SMSSignName {
		/**
		 * 公司签名
		 */
		String SIGN_COMPANY = "纵横世纪";
	}
	
	/**
	 * 已申请短信模板
	 */
	interface SMSTemplateName {
		/**
		 * 短信验证码
		 */
		String VCODE =  "SMS_217925309";
		/**
		 * 签章忘记密码
		 */
		String VCODE_SIGN_FORGET_PASSWORD =  "SMS_218896255";
		/**
		 * 物业端添加操作员-成功通知初始密码
		 */
		String ADD_OPERATOR = "SMS_218031262";
		/**
		 * 物业端操作员-重置密码
		 */
		String RESET_PASSWORD = "SMS_218172247";
	}
	
}