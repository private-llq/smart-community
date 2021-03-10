package com.jsy.community.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 身份证信息算法类
 * @author  MR丶C
 *
 */
public class CardUtil {

    /**
     * 根据身份证的号码算出当前身份证持有者的性别和年龄 18位身份证
     */
    public static Map<String, Object> getCarInfo(String idCard) {
        Map<String, Object> map = new HashMap<>();
        //得到年份
        String year = idCard.substring(6).substring(0, 4);
        //得到月份
        String yue = idCard.substring(10).substring(0, 2);
        String sex;
        // 判断性别
        if (Integer.parseInt(idCard.substring(16).substring(0, 1)) % 2 == 0) {
            sex = "女";
        } else {
            sex = "男";
        }
        // 得到当前的系统时间
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 当前年份
        String fYear = format.format(date).substring(0, 4);
        // 月份
        String fYue = format.format(date).substring(5, 7);
        int age = 0;
        // 当前月份大于用户出身的月份表示已过生
        age = Integer.parseInt(fYear) - Integer.parseInt(year);
        if (Integer.parseInt(yue) <= Integer.parseInt(fYue)) {
            age = age + 1;
        }
        map.put("sex", sex);
        map.put("age", age);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(getSexAndAge(null));
    }

    /**
     * 15位身份证的验证
     */
    public static Map<String, Object> getCarInfo15W(String card)
    {
        Map<String, Object> map = new HashMap<>(2);
        // 年份
        String uYear = "19" + card.substring(6, 8);
        // 月份
        String uYue = card.substring(8, 10);
        // 用户的性别
        String uSex = card.substring(14, 15);
        String sex;
        if (Integer.parseInt(uSex) % 2 == 0) {
            sex = "女";
        } else {
            sex = "男";
        }
        // 得到当前的系统时间
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 当前年份
        String fYear = format.format(date).substring(0, 4);
        // 月份
        String fYue = format.format(date).substring(5, 7);
        int age = 0;
        age = Integer.parseInt(fYear) - Integer.parseInt(uYear);
        // 当前月份大于用户出身的月份表示已过生
        if (Integer.parseInt(uYue) <= Integer.parseInt(fYue)) {
            age = age + 1;
        }
        map.put("sex", sex);
        map.put("age", age);
        return map;
    }

    public static Map<String,Object> getSexAndAge(String idCard){
        Map<String, Object> map = new HashMap<>(2);
        map.put("sex", "未知");
        map.put("age", "未知");
        if( Objects.nonNull(idCard) && idCard.length() ==  18 ){
            map = getCarInfo(idCard);
        }
        if( Objects.nonNull(idCard) && idCard.length() ==  15 ){
            map = getCarInfo15W(idCard);
        }
        return map;
    }



}
