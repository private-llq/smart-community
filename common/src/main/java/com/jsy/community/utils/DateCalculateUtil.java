package com.jsy.community.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author DKS
 * @description 日期工具类
 * @since 2021/8/18  10:18
 **/
public class DateCalculateUtil {
	public static Date getFirstSecondOfMonth(Date date) {
		date = DateUtils.truncate(date, Calendar.MONTH); // 截取日期到月份
		return date;
	}

	public static Date getLastSecondOfMonth(Date date) {
		date = DateUtils.ceiling(date, Calendar.MONTH); // 向上进位月份
		date = DateUtils.addSeconds(date, -1); // 减1秒
		return date;
	}
	
	public static Date getFirstSecondOfYear(Date date) {
		date = DateUtils.truncate(date, Calendar.YEAR); // 截取日期到年份
		return date;
	}
	
	public static Date getLastSecondOfYear(Date date) {
		date = DateUtils.ceiling(date, Calendar.YEAR); // 向上进位年
		date = DateUtils.addSeconds(date, -1); // 减1秒
		return date;
	}
	
	/**
	 * @author DKS
	 * @description 通过年份获取年第一天
	 * @since 2021/8/18  10:18
	 **/
	public static String getFirstYearDateOfAmount(int amount) throws ParseException {
		Date date = new Date();
		Date date1 = DateUtils.setYears(date, amount);
		Date startTimeOfYear = getFirstSecondOfYear(date1);
		String dateStr = String.valueOf(startTimeOfYear);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * @author DKS
	 * @description 通过年份获取年最后一天
	 * @since 2021/8/18  10:18
	 **/
	public static String getLastYearDateOfAmount(int amount) throws ParseException {
		Date date = new Date();
		Date date1 = DateUtils.setYears(date, amount);
		Date endTimeOfYear = getLastSecondOfYear(date1);
		String dateStr = String.valueOf(endTimeOfYear);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * @author DKS
	 * @description 通过月份获取月第一天
	 * @since 2021/8/18  10:18
	 **/
	public static String getFirstMouthDateOfAmount(int amount) throws ParseException {
		Date date = new Date();
		Date date1 = DateUtils.setMonths(date, amount - 1);
		Date firstSecondOfMonth = getFirstSecondOfMonth(date1);
		String dateStr = String.valueOf(firstSecondOfMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * @author DKS
	 * @description 通过月份获取月最后一天
	 * @since 2021/8/18  10:18
	 **/
	public static String getLastMouthDateOfAmount(int amount) throws ParseException {
		Date date = new Date();
		Date date1 = DateUtils.setMonths(date, amount - 1);
		Date lastSecondOfMonth = getLastSecondOfMonth(date1);
		String dateStr = String.valueOf(lastSecondOfMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
//	public static void main(String[] args) throws ParseException {
//		String firstYearDateOfAmount = getFirstYearDateOfAmount(2019);
//		System.out.println(firstYearDateOfAmount);
//		String lastYearDateOfAmount = getLastYearDateOfAmount(2019);
//		System.out.println(lastYearDateOfAmount);
//		String firstMouthDateOfAmount = getFirstMouthDateOfAmount(9);
//		System.out.println(firstMouthDateOfAmount);
//		String lastMouthDateOfAmount = getLastMouthDateOfAmount(9);
//		System.out.println(lastMouthDateOfAmount);
//	}
}
