package com.jsy.community.dto.signature;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 签章用户新传参实体类 (同步实名认证在用)
 * @since 2021-05-27 16:40
 **/
@Data
public class SignResult<T> implements Serializable {
	public SignResult() {
	}
	
	/**
	 * 业务状态码
	 */
	private Integer code;
	
	/**
	 * 响应描述信息
	 */
	private String message;
	
	/**
	 * 返回数据
	 */
	private T data;
	
	/**
	 * 时间戳
	 */
	private Long time;
	
	private String sign;
	
	
	/**
	 * 成功响应-带返回数据
	 */
	public static <T> SignResult<T> success(T data) {
		SignResult<T> result = new SignResult<>(0,"ok");
		result.setData(data);
		return result;
	}
	
	
	/**
	 * 失败响应-自定义错误
	 */
	public static SignResult<Void> fail(Integer code, String msg) {
		return new SignResult<>(code, msg);
	}
	
	
	public SignResult(Integer code, String message) {
		this.code = code;
		this.message = message;
		time = System.currentTimeMillis();
	}
	
	
	
}
