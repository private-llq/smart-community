package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsMenuEntity;

import java.util.List;

/**
 * @Description: 短信套餐
 * @author: DKS
 * @since: 2021/12/9 10:39
 */
public interface ISmsMenuService extends IService<SmsMenuEntity>{
    
    /**
     * @Description: 查询短信套餐列表
     * @author: DKS
     * @since: 2021/12/9 11:10
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsMenuEntity>
     */
    List<SmsMenuEntity> selectSmsMenu();
}
