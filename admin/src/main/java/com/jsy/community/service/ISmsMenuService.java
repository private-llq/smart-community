package com.jsy.community.service;

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
     * @Description: 新增短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:10
     * @Param: [smsMenuEntity]
     * @return: boolean
     */
    boolean addSmsMenu(SmsMenuEntity smsMenuEntity);
    
    /**
     * @Description: 修改短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:10
     * @Param: [smsMenuEntity]
     * @return: boolean
     */
    boolean updateSmsMenu(SmsMenuEntity smsMenuEntity);
    
    /**
     * @Description: 删除短信套餐
     * @author: DKS
     * @since: 2021/12/9 11:10
     * @Param: [id]
     * @return: boolean
     */
    boolean deleteSmsMenu(List<Long> id);
    
    /**
     * @Description: 查询短信套餐列表
     * @author: DKS
     * @since: 2021/12/9 11:10
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsMenuEntity>
     */
    List<SmsMenuEntity> selectSmsMenu();
}
