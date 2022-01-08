package com.jsy.community.exception;

import com.jsy.community.constant.ConstError;
import com.zhsj.basecommon.enums.ErrorEnum;
import com.zhsj.basecommon.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

//@EqualsAndHashCode(callSuper = true)
@Data
public class JSYException extends BaseException {
    private Integer code;

    public JSYException(Integer code, String message) {
        super(code, message);
        this.code = code;
    }

    public JSYException(String message) {
        super(ConstError.NORMAL, message);
        this.code = ConstError.NORMAL;
    }

    public JSYException() {
        this(JSYError.INTERNAL);
    }

    public JSYException(JSYError error) {
        super(error.getCode(), error.getMessage());
        this.code = error.getCode();
    }
}
