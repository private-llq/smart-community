package com.jsy.community.utils;

import org.apache.http.client.methods.HttpGet;

import java.io.*;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * @author chq459799974
 * @description Base64工具类
 * @since 2021-01-29 10:06
 **/
public class Base64Util {
	
	/**
	* @Description: base64转图片
	 * @Param: [imgStr, path]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static boolean base64StrToImage(String imgStr, String path) {
		if (imgStr == null)
			return false;
		Decoder decoder = Base64.getDecoder();
		try {
			// 解密
			byte[] b = decoder.decode(imgStr);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			// 文件夹不存在则自动创建
			File tempFile = new File(path);
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
	* @Description: 图片转base64
	 * @Param: [imgFile]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static String imageToBase64Str(String imgFile) {
		InputStream inputStream = null;
		byte[] data = null;
		try {
			inputStream = new FileInputStream(imgFile);
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
	* @Description: 网络图片转Base64
	 * @Param: [netPicUrl]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/1/29
	**/
	public static String netPicToBase64(String netPicUrl){
		HttpGet httpGet = MyHttpUtils.httpGetWithoutParams(netPicUrl);
		byte[] data = (byte[]) MyHttpUtils.exec(httpGet,MyHttpUtils.ANALYZE_TYPE_BYTE);
		return byteToBase64(data);
	}
	
	public static void main(String[] args) {
		String base64Str = imageToBase64Str("E:/face666.jpg");
		System.out.println(base64Str);
		boolean b = base64StrToImage(base64Str, "E:/转出结果.jpg");
		System.out.println(b);
	}
}
