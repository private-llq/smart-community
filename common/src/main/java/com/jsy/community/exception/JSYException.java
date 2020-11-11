package com.jsy.community.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JSYException extends RuntimeException {
	private Integer code;
	
	public JSYException(Integer code, String message) {
		super(message);
		this.code = code;
	}
	
	public JSYException() {
		this(JSYError.INTERNAL);
	}
	
	public JSYException(JSYError error) {
		super(error.getMessage());
		this.code = error.getCode();
	}
}
