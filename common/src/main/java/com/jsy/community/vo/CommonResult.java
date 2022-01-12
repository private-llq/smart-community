package com.jsy.community.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> implements Serializable {
	@ApiModelProperty("接口返回状态码，0表示成功，1表示一般错误")
	private int code;

	@JsonInclude()
	@ApiModelProperty("接口错误返回提示信息")
	private String message;

	@JsonInclude()
	@ApiModelProperty("具体数据体")
	private T data;
	
	public static <T> CommonResult<T> ok(T data) {
		return new CommonResult<>(0, null, data);
	}
	
	public static <T> CommonResult<T> ok(T data,String msg) {
		return new CommonResult<>(0, msg, data);
	}

	public static <T> CommonResult<T> ok(String msg) {
		return new CommonResult<>(0, msg, null);
	}
	
	public static CommonResult<Boolean> ok() {
		return new CommonResult<>(0, "操作成功", true);
	}


	public static CommonResult<Boolean> error(int code, String message) {
		return new CommonResult<>(code, message, false);
	}

	public static <T> CommonResult<T> error(int code, T data) {
		return new CommonResult<>(code, null, data);
	}

	public static CommonResult<Boolean> error(JSYError error) {
		return error(error.getCode(), error.getMessage());
	}
	
	public static CommonResult<Boolean> error(String message) {
		return error(ConstError.NORMAL, message);
	}


}
