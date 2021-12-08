package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsTypeEntity;

import java.util.List;

/**
 * @Description: 短信分类
 * @author: DKS
 * @since: 2021/12/8 10:39
 */
public interface ISmsTypeService extends IService<SmsTypeEntity>{
    /**
     * @Description: 新增短信分类
     * @author: DKS
     * @since: 2021/12/8 10:48
     * @Param: [smsTypeEntity]
     * @return: boolean
     */
    boolean addSmsType(SmsTypeEntity smsTypeEntity);
    
    /**
     * @Description: 修改短信分类
     * @author: DKS
     * @since: 2021/12/8 10:49
     * @Param: [smsTypeEntity]
     * @return: boolean
     */
    boolean updateSmsType(SmsTypeEntity smsTypeEntity);
    
    /**
     * @Description: 删除短信分类
     * @author: DKS
     * @since: 2021/12/8 10:51
     * @Param: [id]
     * @return: boolean
     */
    boolean deleteSmsType(Long id);
    
    /**
     * @Description: 查询短信分类列表
     * @author: DKS
     * @since: 2021/12/8 10:57
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.SmsTypeEntity>
     */
    List<SmsTypeEntity> selectSmsType();
}
