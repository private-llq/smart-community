package com.jsy.community.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author chq459799974
 * @since 2020-12-11 09:46
 **/
public class MyMathUtils {
	
	static Random random = new Random();
	
	/**
	* @Description: 生成指定规模随机整数
	 * @Param: [scale]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	public static String randomCode(int scale) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < scale; i++) {
			str.append(random.nextInt(10));//0-10随机整数
		}
		return str.toString();
	}
	
	/**
	* @Description: 对象同时持有多种状态  拆解出每种状态码  typeScale表示有多少种状态类型,暂时最大支持30
	 * @Param: [code, scale]
	 * @Return: java.util.List<java.lang.Long>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	public static List<Long> analysisTypeCode(long typeCode, int typeScale){
		if(typeScale > 30){
			typeScale = 30;
		}
		List<Long> codes = new ArrayList<>();
		for(int i=0;i<typeScale;i++) {
			if((typeCode & 1<<i) != 0){
				codes.add(typeCode & 1<<i);
			}
		}
		return codes;
	}
	
	/*默认30*/
	public static List<Long> analysisTypeCode(long typeCode){
		return analysisTypeCode(typeCode,30);
	}
	
	/**
	* @Description: 对象同时持有多种状态 得到最终的状态表示码
	 * @Param: [codes]
	 * @Return: long
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	**/
	public static long getTypeCode(List<Long> codes){
		long typeCode = 0;
		if(codes == null){
			return typeCode;
		}
		for(long num : codes){
			typeCode = typeCode | num;
		}
		return typeCode;
	}
	
	private static void main(String[] args) {
//		for (int i = 0; i < 20; i++) {
//			System.out.println(randomCode(7));
//		}
		
		LinkedList<Long> codes = new LinkedList<>();
		for(int i=0;i<63;i++){
			codes.add(1l<<i);
		}
		long typeCode = getTypeCode(codes);
		System.out.println(typeCode);
		System.out.println(analysisTypeCode(typeCode, 31));
		System.out.println(analysisTypeCode(typeCode));
	}
	
}
