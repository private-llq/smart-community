package com.jsy.community.utils;

import java.text.SimpleDateFormat;

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
        String s=sdfTime.format(System.currentTimeMillis()).replaceAll("[[\\s-:punct:]]", "");
        Long s1=(long) (Math.random() * 9999999999L);
        return s + s1;
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
        String s=sdfTime.format(System.currentTimeMillis()).replaceAll("[[\\s-:punct:]]", "");
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
