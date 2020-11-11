package com.jsy.community.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.qo.BaseQO;

/**
 * @Description: 分页相关工具类
 * @Author chq459799974
 * @Date 2020/11/11 10:56
 **/
public class MyPageUtils {
    /**
    * @Description: 设置分页参数page,size
     * @Param: [page, baseQO]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    public static void setPageAndSize(Page page, BaseQO baseQO){
        if(baseQO.getPage() != null && baseQO.getPage() != 0){
            page.setCurrent(baseQO.getPage());
        }
        if(baseQO.getSize() != null && baseQO.getSize() != 0){
            page.setSize(baseQO.getSize());
        }

    }
}
