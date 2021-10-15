package com.jsy.community.service;

import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;

/**
 * 大后台端异常类
 *
 * @author DKS
 * @since 2021-10-12 16:10
 */
public class AdminException extends JSYException {
	public AdminException(Integer code, String message) {
		super(code, message);
	}
	
	public AdminException() {
		super();
	}
	
	public AdminException(String message) {
		super(ConstError.NORMAL, message);
	}
	
	public AdminException(JSYError error) {
		super(error);
	}
}
