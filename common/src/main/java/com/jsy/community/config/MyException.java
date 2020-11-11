package com.jsy.community.config;
/**
 * 
 * 自定义异常类
 *
 */
public class MyException extends RuntimeException{
	
	private static final long serialVersionUID = -9092171210957548713L;

	public MyException(String msg,Throwable e){
		super(msg,e);
	}
	
}
