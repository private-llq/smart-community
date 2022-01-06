package com.jsy.community.exception;

import com.jsy.community.constant.ConstError;
import com.zhsj.basecommon.enums.ErrorEnum;
import com.zhsj.basecommon.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;

//@EqualsAndHashCode(callSuper = true)
@Data
public class JSYException extends BaseException {
    private String message;
    private Integer code;

    public JSYException() {
        super(JSYError.INTERNAL.getCode(), JSYError.INTERNAL.getMessage());
        this.code = JSYError.INTERNAL.getCode();
        this.message = JSYError.INTERNAL.getMessage();
    }

    public JSYException(JSYError error) {
        super(error.getCode(), error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public JSYException(Integer code, String message) {
        super(code, message);
        this.code = code;
        this.message = message;
    }

    public JSYException(String message) {
        super(50001, message);
        this.code = ConstError.NORMAL;
        this.message = message;
    }

    public JSYException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public void setMessage(String message) {
        super.getErrorEnum().setMsg(message);
        this.message = message;
    }

    public void setCode(Integer code) {
        super.getErrorEnum().setCode(code);
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
