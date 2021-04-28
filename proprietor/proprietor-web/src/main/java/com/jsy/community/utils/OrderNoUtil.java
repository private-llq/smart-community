package com.jsy.community.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: pay
 * @description: 生成唯一订单号
 * @author: Hu
 * @create: 2021-01-22 10:19
 **/
public class OrderNoUtil {

    /**
     * @Description: 支付下单订单号
     * @author: Hu
     * @since: 2021/2/23 17:52
     * @Param:
     * @return:
     */
    public static String getOrder() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=sdfTime.format(new Date().getTime()).replaceAll("[[\\s-:punct:]]", "");
        int s1=(int) (Math.random() * 999999999);
        int s2=(int) (Math.random() * 9);
        return s + s1 + s2;
    }

    public static void main(String[] args) {
        System.out.println(getOrder());
        System.out.println(new Date().getTime());
    }

    /**
     * @Description: 企业付款订单号
     * @author: Hu
     * @since: 2021/2/23 17:52
     * @Param:
     * @return:
     */
    public static String txOrder() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s=sdfTime.format(new Date().getTime()).replaceAll("[[\\s-:punct:]]", "");
        s+=(char) (Math.random() * 26 + 'a');
        s+=(int) (Math.random() * 99);
        s+=(char) (Math.random() * 26 + 'A');
        s+=(int) (Math.random() * 9);
        s+=(char) (Math.random() * 26 + 'a');
        s+=(char) (Math.random() * 26 + 'A');
        s+=(int) (Math.random() * 9);
        s+=(char) (Math.random() * 26 + 'a');
        s+=(char) (Math.random() * 26 + 'A');
        return s;
    }
}
