package com.jsy.community.exception;

import com.jsy.community.api.LeaseException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理
 *
 * @author ling
 * @since 2020-11-11 11:14
 */
@RestControllerAdvice
@Slf4j
public class LeaseExceptionHandler extends JSYExceptionHandler {


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<Boolean> handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return CommonResult.error(JSYError.REQUEST_PARAM);
    }

    @ExceptionHandler(LeaseException.class)
    public CommonResult<Boolean> handlerProprietorException(LeaseException e) {
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<Boolean> handleException(Exception e) {
        log.error(e.getMessage(), e);
        if (e instanceof LeaseException) {
            return CommonResult.error(((LeaseException) e).getCode(), e.getMessage());
        }
        return CommonResult.error(JSYError.INTERNAL);
    }

}
