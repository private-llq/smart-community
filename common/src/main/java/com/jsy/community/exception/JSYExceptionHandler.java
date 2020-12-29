package com.jsy.community.exception;

import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author ling
 * @since 2020-11-19 10:49
 */
@Slf4j
public class JSYExceptionHandler {
	@ExceptionHandler(JSYException.class)
	public CommonResult<Boolean> handlerJSYException(JSYException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public CommonResult<Boolean> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public CommonResult<Boolean> handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		return CommonResult.error(JSYError.NOT_SUPPORT_REQUEST_METHOD);
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

	/**
	 * 业务层抛出的运行时异常
	 * @param e		异常对象
	 * @return		返回指定的错误信息
	 */
	@ExceptionHandler(RuntimeException.class)
	public CommonResult<Boolean> handleRuntimeException(RuntimeException e) {
		log.error(e.getMessage(),e);
		//唯一索引 数据重复异常
		if(e.getMessage().contains("DuplicateKeyException")){
			return CommonResult.error(JSYError.DUPLICATE_KEY);
		}
		return CommonResult.error(JSYError.INTERNAL);

	}
}
