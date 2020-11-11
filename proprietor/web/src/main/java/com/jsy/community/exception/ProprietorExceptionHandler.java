package com.jsy.community.exception;

import com.jsy.community.api.ProprietorException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

/**
 * 异常处理
 *
 * @author ling
 * @date 2020-11-11 11:14
 */
@RestControllerAdvice
@Slf4j
public class ProprietorExceptionHandler {
	@ExceptionHandler(ProprietorException.class)
	public CommonResult<Boolean> handlerProprietorException(ProprietorException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(JSYException.class)
	public CommonResult<Boolean> handlerJSYException(JSYException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	/**
	 * 404
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public CommonResult<Boolean> handlerNoFoundException(Exception e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.NOT_FOUND);
	}
	
	@ExceptionHandler(DuplicateKeyException.class)
	public CommonResult<Boolean> handleDuplicateKeyException(DuplicateKeyException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.DUPLICATE_KEY);
	}
	
	@ExceptionHandler(Exception.class)
	public CommonResult<Boolean> handleException(Exception e) {
		log.error(e.getMessage(), e);
		if (e instanceof ProprietorException) {
			return CommonResult.error(((ProprietorException) e).getCode(), e.getMessage());
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
}
