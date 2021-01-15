package com.jsy.community.exception;

import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
	
	@ExceptionHandler(DuplicateKeyException.class)
	public CommonResult<Boolean> handleException(DuplicateKeyException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.DUPLICATE_KEY);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public CommonResult<Boolean> handleException(HttpRequestMethodNotSupportedException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.BAD_REQUEST);
	}
	/**
	 * 请求Content-type类型错误
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public CommonResult<Boolean> handleHttpMediaTypeNotSupportedExceptionException(HttpMediaTypeNotSupportedException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.NOT_SUPPORT_REQUEST_METHOD);
	}

	/**
	 * 缺失请求body
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public CommonResult<Boolean> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}
}
