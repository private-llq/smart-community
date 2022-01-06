package com.jsy.community.exception;

import com.jsy.community.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 后台异常处理
 *
 * @author ling
 * @since 2020-11-19 11:01
 */
@RestControllerAdvice
@Slf4j
public class AdminExceptionHandler extends JSYExceptionHandler {
	
	private static final Map<String,String> UNI_INDEX_MAP = new HashMap<>();
	static {
		UNI_INDEX_MAP.put("'t_community.uni_lon_lat'","社区经纬度重复");
	}

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
		String msg = UNI_INDEX_MAP.get(e.getMessage().substring(e.getMessage().lastIndexOf("key") + 4));
		if(msg != null){
			return CommonResult.error(JSYError.DUPLICATE_KEY.getCode(), msg);
		}
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
	 * 缺失请求参数
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public CommonResult<Boolean> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		if( e != null && Objects.requireNonNull(e.getMessage()).contains("moduleName") ){
			return CommonResult.error("缺少moduleName!");
		}
		return CommonResult.error(JSYError.REQUEST_PARAM);
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
