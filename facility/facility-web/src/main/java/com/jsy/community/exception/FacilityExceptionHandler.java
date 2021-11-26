package com.jsy.community.exception;

import com.jsy.community.api.FacilityException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理
 *
 * @author ling
 * @since 2020-11-18 14:36
 */
//@RestControllerAdvice
@Slf4j
public class FacilityExceptionHandler extends JSYExceptionHandler {
	@ExceptionHandler(FacilityException.class)
	public CommonResult<Boolean> handlerProprietorException(FacilityException e) {
//		return CommonResult.error(e.getCode(), e.getMessage());
		return CommonResult.error(400, e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public CommonResult<Boolean> handleException(Exception e) {
		log.error(e.getMessage(), e);
		if (e instanceof FacilityException) {
//			return CommonResult.error(((FacilityException) e).getCode(), e.getMessage());
			return CommonResult.error(400, e.getMessage());
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
	
	@ExceptionHandler({HttpMessageNotReadableException.class, UnsatisfiedServletRequestParameterException.class})
	public CommonResult<Boolean> handleExceptionForBadRequest(Exception e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}
}
