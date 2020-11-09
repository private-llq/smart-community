package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T extends Serializable> implements Serializable {
	@ApiModelProperty("接口返回状态码，0表示成功")
	private int code;
	
	@ApiModelProperty("接口错误返回提示信息")
	private String message;
	
	@ApiModelProperty("具体数据体")
	private T data;
	
	public static <T extends Serializable> CommonResult<T> ok(T data) {
		return new CommonResult<>(0, null, data);
	}
	
	public static CommonResult<Boolean> error(int code, String message) {
		return new CommonResult<>(code, message, false);
	}
	
	public static CommonResult<Boolean> error(int code) {
		return error(code, null);
	}
}
