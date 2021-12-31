package com.jsy.community.constant;

/**
 * 错误码code
 *
 * @author ling
 * @since 2020-11-12 16:32
 */
public interface ConstError {
	/**
	 * 一般错误
	 */
	int NORMAL = 1;
	int BAD_REQUEST = 400;
	int UNAUTHORIZED = 401;
	int FORBIDDEN = 403;
	int NOT_FOUND = 404;
	int NOT_SUPPORT_REQUEST_METHOD = 405;
	int REQUEST_PARAM = 499;
	int INTERNAL = 500;
	int NOT_IMPLEMENTED = 501;
	int GATEWAY = 502;
	int DUPLICATE_KEY = 503;
	//自定义错误
	/**
	 * 用户未实名认证
	 */
	int NO_REAL_NAME_AUTHENTICATION = 40001;
	/**
	 * 用户房屋待认证
	 */
	int NO_AUTH_HOUSE = 40002;
	/**
	 * 余额不足
	 */
	int NOT_ENOUGH = 40003;
	/**
	 * 数据丢失
	 */
	int DATA_LOST = 4041;
	
	/**
	 * 楼层超出
	 */
	int FLOOR_BEYOND = 40004;
}
