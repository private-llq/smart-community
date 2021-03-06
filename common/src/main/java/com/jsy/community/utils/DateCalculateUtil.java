package com.jsy.community.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.format.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
	
	public static Date getFirstSecondOfDay(Date date) {
		date = DateUtils.truncate(date, Calendar.DATE); // 截取日期到日
		return date;
	}
	
	public static Date getLastSecondOfDay(Date date) {
		date = DateUtils.ceiling(date, Calendar.DATE); // 向上进位
		return date;
	}
	
	public static String getFirstDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		return simpleDateFormat.format(date);
	}
	
	public static String getLastDate(Date date) {
		date = DateUtils.ceiling(date, Calendar.DATE); // 向上进位
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		return simpleDateFormat.format(date);
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

	// 通过月份获取月第一天
	public static String getFirstMouthDateOfAmount(String amount) throws ParseException {
		LocalDate parse = LocalDate.parse(amount, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Date date = new Date();
		date = DateUtils.setMonths(date, parse.getMonthValue() - 1);
		date = DateUtils.setYears(date, parse.getYear());
		Date firstSecondOfMonth = getFirstSecondOfMonth(date);
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

	// 通过月份获取月最后一天
	public static String getLastMouthDateOfAmount(String amount) throws ParseException {
		LocalDate parse = LocalDate.parse(amount, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Date date = new Date();
		date = DateUtils.setYears(date, parse.getYear());
		date = DateUtils.setMonths(date, parse.getMonthValue() - 1);
		Date lastSecondOfMonth = getLastSecondOfMonth(date);
		String dateStr = String.valueOf(lastSecondOfMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * @author DKS
	 * @description 通过日获取当天
	 * @since 2021/8/19  14:18
	 **/
	public static String getFirstDayDateOfAmount(int amount) throws ParseException {
		Date date = new Date();
		Date date1 = DateUtils.setDays(date, amount);
		Date startTimeOfDay = getFirstSecondOfDay(date1);
		String dateStr = String.valueOf(startTimeOfDay);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * @author DKS
	 * @description 通过日获取第二天
	 * @since 2021/8/19  14:18
	 **/
	public static String getLastDayDateOfAmount(int amount) throws ParseException {
		Date date = new Date();
		Date date1 = DateUtils.setDays(date, amount);
		Date endTimeOfDay = getLastSecondOfDay(date1);
		String dateStr = String.valueOf(endTimeOfDay);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d = sdf.parse(dateStr);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * 格林威治时间字符串转本地时间Date再转成localDate
	 * @param: [strDate]
	 * @return: java.util.Date
	 * @author: DKS
	 * @date: 2021/9/8 9:19
	 */
	public static LocalDate gtmToLocalDate(String strDate) {
		String DATE_PATTERN = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
		Date d;
		LocalDate localDate = null;
		try {
			if(StringUtils.isNotBlank(strDate)){
				d = sdf.parse(strDate);
				sdf = new SimpleDateFormat(DATE_PATTERN);
				org.joda.time.format.DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN);
				d=fmt.parseLocalDateTime(sdf.format(d)).toDate();
				localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
		} catch (org.apache.http.ParseException | java.text.ParseException e) {
			e.printStackTrace();
		}
		return localDate;
	}

	/**
	 * @author: Pipi
	 * @description: 获取当前月份是奇数月还是偶数月
	 * @param :
	 * @return: {@link Integer}
	 * @date: 2022/1/11 14:40
	 **/
	public static Integer getCurrentMonthOddEven() {
		int monthValue = LocalDate.now().getMonthValue();
		if (monthValue % 2 == 0) {
			return 2;
		} else {
			return 1;
		}
	}

	/**
	 * @author: Pipi
	 * @description: 判断当日加三天之后,是否还是这个月
	 * @param :
	 * @return: {@link Boolean}
	 * @date: 2022/1/17 17:33
	 **/
	public static Boolean getLastThridDayOfMonth() {
		LocalDate localDate = LocalDate.now();
		int monthValue = localDate.getMonthValue();
		int monthValue1 = localDate.plusDays(3).getMonthValue();
		return monthValue == monthValue1;
	}

	
	public static void main(String[] args) throws ParseException {
		String firstYearDateOfAmount = getFirstYearDateOfAmount(2019);
		System.out.println(firstYearDateOfAmount);
		String lastYearDateOfAmount = getLastYearDateOfAmount(2019);
		System.out.println(lastYearDateOfAmount);
		String firstMouthDateOfAmount = getFirstMouthDateOfAmount(9);
		System.out.println(firstMouthDateOfAmount);
		String lastMouthDateOfAmount = getLastMouthDateOfAmount(9);
		System.out.println(lastMouthDateOfAmount);
		String firstDayDateOfAmount = getFirstDayDateOfAmount(30);
		System.out.println(firstDayDateOfAmount);
		String lastDayDateOfAmount = getLastDayDateOfAmount(30);
		System.out.println(lastDayDateOfAmount);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
		Date date = simpleDateFormat.parse("2021-07-02");
		System.out.println(date);   //Fri Jul 02 00:00:00 CST 2021
		System.out.println(simpleDateFormat.format(date));  //2017-07-02
		Date firstSecondOfDay = getFirstSecondOfDay(date);
		System.out.println(firstSecondOfDay);
		
		String firstDate = getFirstDate(date);
		System.out.println(firstDate);
		String lastDate = getLastDate(date);
		System.out.println(lastDate);
		
	}
}
