package com.jsy.community.exception;

import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 后台异常处理
 *
 * @author ling
 * @since 2020-11-19 11:01
 */
@RestControllerAdvice
@Slf4j
public class AdminExceptionHandler {
	@ExceptionHandler(JSYException.class)
	public CommonResult<Boolean> handlerProprietorException(JSYException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public CommonResult<Boolean> handleException(Exception e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.INTERNAL);
	}
}