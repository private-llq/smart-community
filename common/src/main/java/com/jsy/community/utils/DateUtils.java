package com.jsy.community.utils;

import org.redisson.executor.CronExpression;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author YuLF
 * @since 2021-02-01 10:08
 */
public class DateUtils {

    /**
     * 【分布式模块下面只执行一次定时任务】
     * 验证当前任务的执行时间是否在指定的时间运行，如果不是则说明在这之前已经执行过一次了，任务需要加锁才会生效,
     * 不加锁的情况下 任何模块都是同一时间执行无法看出效果
     * 同时cron表达式只能为 定时某个点执行(如每天定时9点) 表达式 不能为间隔的表达式如(没间隔1天执行或没间隔5分钟执行) 这个拿到的时间是不一样的
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

}
