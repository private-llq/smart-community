package com.jsy.community.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.jsy.community.config.MyException;
import org.springframework.util.StringUtils;


public class ValueAssertUtil {
	
	//=========================0或null判断=============================================
	
	public static void assertIntegerValue(Integer... params){
		for (Integer param : params) {
			if(param == null || param == 0){
				throw new MyException("接口请求参数错误 必填参数为  0 or null",new Throwable());
			}
		}
	}
	
	public static void assertIntegerValue(String errorMsg, Integer... params){
		for (Integer param : params) {
			if(param == null || param == 0){
				throw new MyException(errorMsg,new Throwable());
			}
		}
	}
	
	public static void assertLongValue(Long... params){
		for (Long param : params) {
			if(param == null || param == 0L){
				throw new MyException("接口请求参数错误 必填参数为  0 or null",new Throwable());
			}
		}
	}
	
	public static void assertLongValue(String errorMsg, Long... params){
		for (Long param : params) {
			if(param == null || param == 0L){
				throw new MyException(errorMsg,new Throwable());
			}
		}
	}
	
	public static void assertDoubleValue(Double... params){
		for (Double param : params) {
			if(param == null || param == 0D){
				throw new MyException("接口请求参数错误 必填参数为  0 or null",new Throwable());
			}
		}
	}
	
	public static void assertDoubleValue(String errorMsg, Double... params){
		for (Double param : params) {
			if(param == null || param == 0D){
				throw new MyException(errorMsg,new Throwable());
			}
		}
	}
	
	public static void assertBigDecimalValue(BigDecimal... params){
		for (BigDecimal param : params) {
			if(param == null || BigDecimal.ZERO.equals(param)){
				throw new MyException("接口请求参数错误 必填参数为  0 or null",new Throwable());
			}
		}
	}
	
	public static void assertBigDecimalValue(String errorMsg, BigDecimal... params){
		for (BigDecimal param : params) {
			if(param == null || BigDecimal.ZERO.equals(param)){
				throw new MyException(errorMsg,new Throwable());
			}
		}
	}
	
	public static void assertStringValue(String... params){
		for (String param : params) {
			if(StringUtils.isEmpty(param)){
				throw new MyException("接口请求参数错误 必填参数为 空字符串   or null",new Throwable());
			}
		}
	}
	
//	public static void assertStringValue(String errorMsg, String[] params){
//		for (String param : params) {
//			if(StringUtils.isEmpty(param)){
//				throw new MyException(errorMsg,new Throwable());
//			}
//		}
//	}
	
	//=========================固定值判断===================================================
	
	public static void assertFixedIntegerValue(String errorMsg, List<Integer> expectedValues, Integer... params){
		for (Integer param : params) {
			if(!expectedValues.contains(param)){
				throw new MyException(errorMsg,new Throwable());
			}
		}
	}
	
}
