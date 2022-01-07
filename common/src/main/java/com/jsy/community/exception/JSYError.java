package com.jsy.community.exception;

import com.jsy.community.constant.ConstError;

/**
 * 错误枚举
 *
 * @author ling
 * @since 2020-11-11 11:02
 */
public enum JSYError {
	/**
	 * 错误请求
	 * code的定义:物业模块1,租赁模块2,业主模块3,支付模块4,大后台5
	 * 第二位统一为6,然后从001开始计数,例如物业模块第一个就为16001
	 */
	BAD_REQUEST(ConstError.BAD_REQUEST, "错误的请求"),
	UNAUTHORIZED(ConstError.UNAUTHORIZED, "未认证"),
	FORBIDDEN(ConstError.FORBIDDEN, "禁止访问"),
	NOT_FOUND(ConstError.NOT_FOUND, "页面丢失了"),
	NOT_SUPPORT_REQUEST_METHOD(ConstError.NOT_SUPPORT_REQUEST_METHOD, "不支持的请求类型"),
	REQUEST_PARAM(ConstError.REQUEST_PARAM, "请求参数错误"),
	INTERNAL(ConstError.INTERNAL, "服务器错误"),
	NOT_IMPLEMENTED(ConstError.NOT_IMPLEMENTED, "未实现"),
	GATEWAY(ConstError.GATEWAY, "网关错误"),
	DUPLICATE_KEY(ConstError.DUPLICATE_KEY, "数据已存在!请检查重复的数据"),
	NO_REAL_NAME_AUTH(ConstError.NO_REAL_NAME_AUTHENTICATION, "用户未实名认证!"),
	OPERATOR_INFORMATION_NOT_OBTAINED(ConstError.BAD_REQUEST, "未获取到操作员信息!"),
	NO_AUTH_HOUSE(ConstError.NO_AUTH_HOUSE, "房屋待认证!"),
	DATA_LOST(ConstError.DATA_LOST,"数据不存在！"),
	FLOOR_BEYOND(ConstError.FLOOR_BEYOND,"总楼层低于房屋楼层，无法修改！"),

	// 支付模块
	THIRD_FAILED(46001, "第三方服务调用失败!"),
	THIRD_QUERY_FAILED(46002, "查询失败!"),
	DESK_CREATE_ERROR(46003, "收银台失败!"),

	// 租赁模块

	// 业主模块

	// 物业模块

	// 大后台模块
	SMS_TYPE_DUPLICATE(50001, "有存在短信模板的分类无法被删除!"),
	SMS_TYPE_LOST(50002, "有不存在的短信分类"),
	SMS_MENU_LOST(50003, "有不存在的短信套餐"),


	NOT_ENOUGH(ConstError.NOT_ENOUGH,"余额不足！");


	private final Integer code;
	private final String message;
	
	public Integer getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
	
	JSYError(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
