package com.jsy.community.utils.imutils.open;

/**
 * 签名错误异常
 * @author lxjr
 * @date 2021/8/16 15:57
 */
public class SignException extends RuntimeException{
    private static final long serialVersionUID = 4058267689728652630L;

    public SignException(String message) {
        super(message);
    }
}
