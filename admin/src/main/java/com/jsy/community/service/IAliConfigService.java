package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PayConfigureEntity;

/**
 * @program: com.jsy.community
 * @description: 支付宝支付配置
 * @author: DKS
 * @create: 2021-11-10 14:06
 **/
public interface IAliConfigService extends IService<PayConfigureEntity> {
    /**
     * @Description: 更新配置
     * @author: DKS
     * @since: 2021/11/10 14:48
     * @Param: [payConfigureEntity]
     * @return: boolean
     */
    boolean basicConfig(PayConfigureEntity payConfigureEntity);
    
    /**
     * @Description: 查询支付证书状态
     * @author: DKS
     * @since: 2021/11/10 14:55
     * @Param: []
     * @return: com.jsy.community.entity.PayConfigureEntity
     */
    PayConfigureEntity getConfig();
    
    /**
     * @Description: 查询小区支付配置
     * @author: DKS
     * @since: 2021/11/10 14:56
     * @Param: []
     * @return: com.jsy.community.entity.PayConfigureEntity
     */
    PayConfigureEntity getCompanyConfig();
}
