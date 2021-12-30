package com.jsy.community.api;

import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.zhsj.basecommon.enums.ErrorEnum;

/**
 * @author ling
 * @since 2020-11-11 14:13
 */
public class ProprietorException extends JSYException {
	public ProprietorException(Integer code, String message) {
		super(code, message);
	}
	
	public ProprietorException() {
		super();
	}
	
	public ProprietorException(String message) {
		super(ConstError.NORMAL, message);
	}
	
	public ProprietorException(JSYError error) {
		super(error);
	}
	
	public ProprietorException(ErrorEnum errorEnum) {
		super(errorEnum);
	}
}
