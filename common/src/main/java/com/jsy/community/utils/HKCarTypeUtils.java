package com.jsy.community.utils;

/**
 * @author lihao
 * @ClassName HKCarTypeUtils
 * @Date 2021/3/5  19:04
 * @Description TODO
 * @Version 1.0
 **/
public class HKCarTypeUtils {
	
	public static String getCarType(byte number){
		switch (number) {
			case 0:
				return "未知";
			case 1:
				return "客车";
			case 2:
				return "货车";
			case 3:
				return "轿车";
			case 4:
				return "面包车";
			case 5:
				return "小货车";
			case 6:
				return "行人";
			case 7:
				return "二轮车";
			case 8:
				return "三轮车";
			case 9:
				return "SUV/MPV";
			case 10:
				return "中型客车";
			case 11:
				return "机动车";
			case 12:
				return "非机动车";
			case 13:
				return "小型轿车";
			case 14:
				return "微型轿车";
			case 15:
				return "皮卡车";
			case 16:
				return "集装箱卡车";
			case 17:
				return "微卡,栏板车";
			case 18:
				return "渣土车";
			case 19:
				return "吊车,工程车";
			case 20:
				return "油罐车";
			case 21:
				return "混领土搅拌车";
			case 22:
				return "平板拖车";
			case 23:
				return "两厢轿车";
			case 24:
				return "三厢轿车";
			case 25:
				return "轿跑";
			case 26:
				return "小型客车";
		}
		return "未定义";
	}
}
