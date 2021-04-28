package com.jsy.community.utils;

import org.redisson.executor.CronExpression;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author YuLF
 * @since 2021-02-01 10:08
 */
public class DateUtils {

    /**
     * 定时cron设置下面生效
     * 【分布式模块下面只执行一次定时任务】
     * 验证当前任务的执行时间是否在指定的时间运行，如果不是则说明在这之前已经执行过一次了，任务需要加锁才会生效,
     * 不加锁的情况下 任何模块都是同一时间执行无法看出效果
     * 同时cron表达式只能为 定时某个点执行(如每天定时9点) 表达式 不能为间隔的表达式如(每间隔1天执行或每间隔5分钟执行) 这个拿到的时间是不一样的
     */
    public static boolean notNeedImplemented(String cron){
        String contrastTimeFormat = "HH:mm:ss";
        CronExpression c =new CronExpression(cron);
        Date nextValidTimeAfter = c.getNextValidTimeAfter(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(contrastTimeFormat);
        String cronTime = simpleDateFormat.format(nextValidTimeAfter);
        String currentTime = simpleDateFormat.format(new Date());
        return !cronTime.equals(currentTime);
    }

    public static String now(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * 解析两个日期段之间的所有月份
     * @param beginDateStr 开始日期  ，至少精确到yyyy-MM
     * @param endDateStr 结束日期  ，至少精确到yyyy-MM
     * @return yyyy-MM-dd日期集合
     */
    public static List<String> getDayListOfMonth(String beginDateStr, String endDateStr) {
        // 指定要解析的时间格式
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");

        // 定义一些变量
        Date beginDate = null;
        Date endDate = null;

        Calendar beginGC = null;
        Calendar endGC = null;
        List<String> list = new ArrayList<String>();

        try {
            // 将字符串parse成日期
            beginDate = f.parse(beginDateStr);
            endDate = f.parse(endDateStr);

            // 设置日历
            beginGC = Calendar.getInstance();
            beginGC.setTime(beginDate);

            endGC = Calendar.getInstance();
            endGC.setTime(endDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

            // 直到两个时间相同
            while (beginGC.getTime().compareTo(endGC.getTime()) <= 0) {

                list.add(sdf.format(beginGC.getTime()));
                // 以日为单位，增加时间
                beginGC.add(Calendar.MONTH, 1);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
