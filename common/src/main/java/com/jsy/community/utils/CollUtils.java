package com.jsy.community.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 集合工具类
 * @Date: 2021/8/20 9:29
 * @Version: 1.0
 **/
public class CollUtils {
    /**
     * @author: Pipi
     * @description: 将列表按指定数量拆分
     * @param list: 需要拆分的数据
     * @param num: 拆分成多少条一个列表
     * @return: java.util.List<java.util.List<T>>
     * @date: 2021/8/20 9:34
     **/
    public static <T> List<List<T>> spilList(List<T> list, Integer num) {
        List<List<T>> newList = new ArrayList<>();
        Integer size = list.size();
        if (size > num) {
            // 需要拆分
            // 拆分成多个列表
            Integer resuletSize = size / num + 1;
            System.out.println("共有 ： " + size + "条，！" + " 分为 ：" + resuletSize + "批");
            for (Integer i = 0; i < resuletSize; i++) {
                newList.add(list.subList(i * num, (i + 1) * num > size ? size : (i + 1) * num));
            }
        }
        return newList;
    }
}
