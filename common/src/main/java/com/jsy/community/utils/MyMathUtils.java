package com.jsy.community.utils;

import java.util.Random;

/**
 * @author chq459799974
 * @since 2020-12-11 09:46
 **/
public class MyMathUtils {
	
	static Random random = new Random();
	
	public static String randomCode(int scale) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < scale; i++) {
			str.append(random.nextInt(10));//0-10随机整数
		}
		return str.toString();
	}
	
	private static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			System.out.println(randomCode(7));
		}
	}
	
}
