package com.jsy.community.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class TimeUtils {
    public static void main(String[] args) {
        LocalDateTime of = LocalDateTime.of(2020, 1, 1, 12, 10);
        LocalDateTime of1 = LocalDateTime.of(2020, 2, 2, 13, 30);

        HashMap<String, Long> datePoor = getDatePoor(of, of1);
        System.out.println(datePoor);
    }

    public static HashMap<String, Long> getDatePoor(LocalDateTime startTime, LocalDateTime endTime) {

        long nd = 1000 * 24 * 60 * 60;//每天毫秒数

        long nh = 1000 * 60 * 60;//每小时毫秒数

        long nm = 1000 * 60;//每分钟毫秒数

        Duration duration = Duration.between(startTime,endTime); // 获得两个时间的毫秒时间差异
        long diff = duration.toMillis();

        long day = diff / nd;   // 计算差多少天

        long hour = diff % nd / nh; // 计算差多少小时

        long min = diff % nd % nh / nm;  // 计算差多少分钟

        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("day",day);
        hashMap.put("hour",hour);
        hashMap.put("min",min);

        return hashMap;

    }
}
