package com.jsy.community.api;

/**
 * @author lihao
 * @ClassName IHkFaceService
 * @Date 2021/3/13  14:39
 * @Description TODO
 * @Version 1.0
 **/
public interface IHkFaceService {
	
	/**
	 * @return boolean
	 * @Author lihao
	 * @Description 开启人脸比对
	 * @Date 2021/3/13 14:42
	 * @Param [ip, port, sUsername, sPassword]
	 **/
	boolean openFaceCompare(String ip, short port, String sUsername, String sPassword);
}
