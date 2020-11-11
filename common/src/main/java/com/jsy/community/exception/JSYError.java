package com.jsy.community.exception;

/**
 * 错误枚举
 *
 * @author ling
 * @date 2020-11-11 11:02
 */
public enum JSYError {
	BAD_REQUEST(400, "请求无效"),
	UNAUTHORIZED(401, "未认证"),
	FORBIDDEN(403, "禁止访问"),
	NOT_FOUND(404, "页面丢失了"),
	REQUEST_PARAM(499, "请求参数错误"),
	INTERNAL(500, "内部错误"),
	NOT_IMPLEMENTED(501, "未实现"),
	GATEWAY(502, "网关错误"),
	DUPLICATE_KEY(503, "数据库错误");
	
	
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
