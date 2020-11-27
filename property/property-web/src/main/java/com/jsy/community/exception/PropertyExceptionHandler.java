package com.jsy.community.exception;

import com.jsy.community.api.PropertyException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理
 *
 * @author ling
 * @since 2020-11-18 14:36
 */
@RestControllerAdvice
@Slf4j
public class PropertyExceptionHandler extends JSYExceptionHandler {
	@ExceptionHandler(PropertyException.class)
	public CommonResult<Boolean> handlerProprietorException(PropertyException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public CommonResult<Boolean> handleException(Exception e) {
		log.error(e.getMessage(), e);
		if (e instanceof PropertyException) {
			return CommonResult.error(((PropertyException) e).getCode(), e.getMessage());
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public CommonResult<Boolean> handleException(HttpMessageNotReadableException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}
}
