package com.jsy.community.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description Base64工具类
 * @since 2021-01-29 10:06
 **/
public class Base64Util {
	
	/**
	* @Description: base64转文件
	 * @Param: [file64Str, outPath]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static boolean base64StrToFile(String file64Str, String outPath) {
		if (file64Str == null)
			return false;
		Decoder decoder = Base64.getDecoder();
		try {
			// 解密
			byte[] b = decoder.decode(file64Str);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			// 文件夹不存在则自动创建
			File tempFile = new File(outPath);
			if (!tempFile.getParentFile().exists()) {
				tempFile.getParentFile().mkdirs();
			}
			OutputStream out = new FileOutputStream(tempFile);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	* @Description: base64转MultipartFile
	 * @Param: [base64Str]
	 * @Return: org.springframework.web.multipart.MultipartFile
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	public static MultipartFile base64StrToMultipartFile(String base64Str){
		if (StringUtils.isEmpty(base64Str)){
			return null;
		}
		Decoder decoder = Base64.getDecoder();
		MultipartFile multipartFile = null;
		try {
			// 解密
			byte[] b = decoder.decode(base64Str);
			multipartFile = new MockMultipartFile("file.png", b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return multipartFile;
	}
	
	/**
	* @Description: 文件转base64(路径入参)
	 * @Param: [filePath]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static String fileToBase64Str(String filePath) {
		InputStream inputStream;
		byte[] data = null;
		try {
			inputStream = new FileInputStream(filePath);
			data = new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 加密
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(data);
	}
	
	/**
	 * @Description: 文件转base64(文件入参)
	 * @Param: [file]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	 **/
	public static String fileToBase64Str(MultipartFile file) {
		InputStream inputStream;
		byte[] data = null;
		try {
			inputStream = file.getInputStream();
			data = new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 加密
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(data);
	}
	
	/**
	* @Description: byte转Base64
	 * @Param: [data]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static String byteToBase64(byte[] data){
		// 加密
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(data);
	}
	
	/**
	* @Description: 网络文件转Base64
	 * @Param: [netPicUrl]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static String netFileToBase64(String netFileUrl){
		HttpGet httpGet = MyHttpUtils.httpGetWithoutParams(netFileUrl);
		byte[] data = (byte[]) MyHttpUtils.exec(httpGet,MyHttpUtils.ANALYZE_TYPE_BYTE);
		return byteToBase64(data);
	}
	
	public static void main(String[] args) {
		String base64Str = fileToBase64Str("E:/video/VID_20210223_134106.mp4");
		System.out.println(base64Str);
		boolean b = base64StrToFile(base64Str, "E:/video/转出结果.mp4");
		System.out.println(b);
	}
}
