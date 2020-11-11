package com.jsy.community.api;

import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;

/**
 * @author ling
 * @date 2020-11-11 14:13
 */
public class ProprietorException extends JSYException {
	public ProprietorException(Integer code, String message) {
		super(code, message);
	}
	
	public ProprietorException() {
		super();
	}
	
	public ProprietorException(JSYError error) {
		super(error);
	}
}
