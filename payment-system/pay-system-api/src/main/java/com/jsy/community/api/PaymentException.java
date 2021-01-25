package com.jsy.community.api;

import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;

/**
 * @author ling
 * @since 2020-11-11 14:13
 */
public class PaymentException extends JSYException {
	public PaymentException(Integer code, String message) {
		super(code, message);
	}

	public PaymentException() {
		super();
	}

	public PaymentException(String message) {
		super(ConstError.NORMAL, message);
	}

	public PaymentException(JSYError error) {
		super(error);
	}
}
