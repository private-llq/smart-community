package com.jsy.community.api;

import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;

/**
 * @author ling
 * @since 2020-11-11 14:13
 */
public class LeaseException extends JSYException {
    public LeaseException(Integer code, String message) {
        super(code, message);
    }

    public LeaseException() {
        super();
    }

    public LeaseException(String message) {
        super(ConstError.NORMAL, message);
    }

    public LeaseException(JSYError error) {
        super(error);
    }
}
