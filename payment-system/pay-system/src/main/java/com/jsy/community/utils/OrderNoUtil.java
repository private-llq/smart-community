package com.jsy.community.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: pay
 * @description:
 * @author: Hu
 * @create: 2021-01-22 10:19
 **/
public class OrderNoUtil {
    public static String getOrder() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=sdfTime.format(new Date().getTime()).replaceAll("[[\\s-:punct:]]", "");
        int s1=(int) (Math.random() * 999999999);
        int s2=(int) (Math.random() * 9);
        return s + s1 + s2;
    }
}
