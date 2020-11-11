package com.jsy.community.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class JSYException extends Exception implements Serializable {
	private Integer code;
	
	public JSYException(Integer code, String message) {
		super(message);
		this.code = code;
	}
	
	public JSYException(JSYError error) {
		super(error.getMessage());
		this.code = error.getCode();
	}
}
