package com.jsy.community.utils;

import java.util.regex.Pattern;

/**
 * 常用正则
 *
 * @author ling
 * @since 2020-11-12 17:02
 */
@SuppressWarnings("unused")
public class RegexUtils {

	/**
	 * 正则表达式：验证只能输入特定的字符
	 */
	public static final String REGEX_DATE = "^[年|月|周|日]{1}";


	/**
	 * 年代正则表达式：匹配用户选择的是否是1930~当前年
	 */
	//public static final String REGEX_YEAR = String.format("^(19[3-9]\\d|20[01]\\d|%s)$", new SimpleDateFormat("yyyy").format(new Date()));
	public static final String REGEX_YEAR = "^[0-9]{4}";
	/**
	 * 正则表达式：验证数字性别、参数只能 0-2
	 */
	public static final String REGEX_GENDER = "^[0-2]{1}";

	/**
	 * 正则表达式：验证中国姓名
	 */
	public static final String REGEX_REAL_NAME = "^[\u4e00-\u9fa5]{2,4}";

	/**
	 * 正则表达式：验证 0-1限制
	 */
	public static final String REGEX_INFORM_STATE = "^[0-1]{1}";

	/**
	 * 正则表达式：验证汽车牌照
	 */
	public static final String REGEX_CAR_PLATE = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";

	/**
	 * 正则表达式：验证用户名
	 */
	public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,20}$";
	
	/**
	 * 正则表达式：验证密码
	 */
	public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";
	
	/**
	 * 正则表达式：验证手机号
	 */
	public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
	
	/**
	 * 正则表达式：验证邮箱
	 */
	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	
	/**
	 * 正则表达式：验证汉字
	 */
	public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],*$";
	
	/**
	 * 正则表达式：验证身份证
	 */
	public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
	
	/**
	 * 正则表达式：验证URL
	 */
	public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	
	/**
	 * 正则表达式：验证IP地址
	 */
	public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

	/**
	 * 校验姓名
	 *
	 * @param nickName 姓名
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isRealName(String nickName) {
		return Pattern.matches(REGEX_REAL_NAME, nickName);
	}

	/**
	 * 校验用户名
	 *
	 * @param username 用户名
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUsername(String username) {
		return Pattern.matches(REGEX_USERNAME, username);
	}
	
	/**
	 * 校验密码
	 *
	 * @param password 密码
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isPassword(String password) {
		return Pattern.matches(REGEX_PASSWORD, password);
	}
	
	/**
	 * 校验手机号
	 *
	 * @param mobile 手机号
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile) {
		return Pattern.matches(REGEX_MOBILE, mobile);
	}
	
	/**
	 * 校验邮箱
	 *
	 * @param email 邮箱地址
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isEmail(String email) {
		return Pattern.matches(REGEX_EMAIL, email);
	}
	
	/**
	 * 校验汉字
	 *
	 * @param chinese 汉字字符串
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isChinese(String chinese) {
		return Pattern.matches(REGEX_CHINESE, chinese);
	}
	
	/**
	 * 校验身份证
	 *
	 * @param idCard 身份证号
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIDCard(String idCard) {
		return Pattern.matches(REGEX_ID_CARD, idCard);
	}
	
	/**
	 * 校验URL
	 *
	 * @param url 网址
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUrl(String url) {
		return Pattern.matches(REGEX_URL, url);
	}

	/**
	 * 校验性别
	 *
	 * @param sex 传入的性别参数
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isSex(String sex) {
		return Pattern.matches(REGEX_GENDER, sex);
	}
	
	/**
	 * 校验IP地址
	 *
	 * @param ipAddr IP地址
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIPAddr(String ipAddr) {
		return Pattern.matches(REGEX_IP_ADDR, ipAddr);
	}
}
