package com.jsy.community.exception;


import com.jsy.community.constant.ConstError;
import com.jsy.community.exception.JSYError;
import com.zhsj.basecommon.enums.ErrorEnum;
import com.zhsj.basecommon.exception.BaseException;


/**
 * @author lxjr
 */
public class MsgException extends BaseException {

    private String message;
    private Integer code;

    public MsgException() {
        super(JSYError.FORBIDDEN.getCode(), JSYError.FORBIDDEN.getMessage());
        this.message = message;
    }

    public MsgException(JSYError resultEnum) {
        super(resultEnum.getCode(), resultEnum.getMessage());
        this.message = resultEnum.getMessage();
        this.code = resultEnum.getCode();
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