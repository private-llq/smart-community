package com.jsy.community.exception;

import com.jsy.community.api.ProprietorException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * 异常处理
 *
 * @author ling
 * @since 2020-11-11 11:14
 */
//@RestControllerAdvice
@Slf4j
public class ProprietorExceptionHandler extends JSYExceptionHandler {
	@ExceptionHandler(ProprietorException.class)
	public CommonResult<Boolean> handlerProprietorException(ProprietorException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public CommonResult<Boolean> handleException(Exception e) {
		log.error(e.getMessage(), e);
		if (e instanceof ProprietorException) {
			return CommonResult.error(((ProprietorException) e).getCode(), e.getMessage());
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
	
	@ExceptionHandler( value = {HttpMessageNotReadableException.class,MissingServletRequestPartException.class})
	public CommonResult<Boolean> handleHttpMessageNotReadableException(Exception e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}

}
