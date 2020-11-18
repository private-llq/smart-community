package com.jsy.community.exception;

import com.jsy.community.api.PropertyException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;


/**
 * 异常处理
 *
 * @author ling
 * @since 2020-11-18 14:36
 */
@RestControllerAdvice
@Slf4j
public class PropertyExceptionHandler {
	@ExceptionHandler(PropertyException.class)
	public CommonResult<Boolean> handlerProprietorException(PropertyException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(JSYException.class)
	public CommonResult<Boolean> handlerJSYException(JSYException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public CommonResult<Boolean> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}
	
	/**
	 * 接口参数错误异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public CommonResult<Boolean> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return CommonResult.error(JSYError.BAD_REQUEST.getCode(), e.getMessage());
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
		if (e instanceof PropertyException) {
			return CommonResult.error(((PropertyException) e).getCode(), e.getMessage());
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
}
