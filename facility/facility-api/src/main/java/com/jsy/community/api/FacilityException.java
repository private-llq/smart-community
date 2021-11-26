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
public class FacilityException extends RuntimeException {
	public FacilityException(Integer code, String message) {
		super();
//		super(code, message);
	}
	
	public FacilityException() {
		super();
	}
	
	public FacilityException(String message) {
//		super(ConstError.NORMAL, message);
		super();

	}
	
	public FacilityException(JSYError error) {
//		super(error);
		super();
	}
}
