package com.jsy.community.api;

import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;

/**
 * 物业端异常类
 *
 * @author ling
 * @since 2020-11-18 14:32
 */
public class PropertyException extends JSYException {
	public PropertyException(Integer code, String message) {
		super(code, message);
	}
	
	public PropertyException() {
		super();
	}
	
	public PropertyException(String message) {
		super(ConstError.NORMAL, message);
	}
	
	public PropertyException(JSYError error) {
		super(error);
	}
}
