package com.jsy.community.exception;

import com.jsy.community.api.PaymentException;
import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: 异常处理
 * @author: Hu
 * @since: 2021/1/25 16:46
 * @Param:
 * @return:
 */
//@RestControllerAdvice
@Slf4j
public class PaymentExceptionHandler extends JSYExceptionHandler {


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public CommonResult<Boolean> handleException(HttpMessageNotReadableException e) {
		log.error(e.getMessage(), e);
		return CommonResult.error(JSYError.REQUEST_PARAM);
	}


	@ExceptionHandler(PaymentException.class)
	public CommonResult<Boolean> handlerProprietorException(PaymentException e) {
		return CommonResult.error(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public CommonResult<Boolean> PaymentException(Exception e) {
		log.error(e.getMessage(), e);
		if (e instanceof PaymentException) {
			return CommonResult.error(((PaymentException) e).getCode(), e.getMessage());
		}
		return CommonResult.error(JSYError.INTERNAL);
	}

}
