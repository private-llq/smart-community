package com.jsy.community.service;

import com.jsy.community.entity.CompanyPayConfigEntity;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 微信支付配置
 * @author: DKS
 * @create: 2021-11-10 14:02
 **/
public interface IWeChatConfigService {
    
    /**
     * @Description: 更新基本配置
     * @author: DKS
     * @since: 2021/11/10 14:23
     * @Param: [communityHardWareEntity]
     * @return: boolean
     */
    boolean basicConfig(CompanyPayConfigEntity communityHardWareEntity);
    
    /**
     * @Description: 查询支付公私钥状态
     * @author: DKS
     * @since: 2021/11/10 14:27
     * @Param: []
     * @return: com.jsy.community.entity.CompanyPayConfigEntity
     */
    CompanyPayConfigEntity getConfig();
    
    /**
     * @Description: 查询微信支付配置
     * @author: DKS
     * @since: 2021/11/10 14:35
     * @Param: [type]
     * @return: com.jsy.community.entity.CompanyPayConfigEntity
     */
    CompanyPayConfigEntity getCompanyConfig(Integer type);
    
    /**
     * @Description: 查询退款配置状态
     * @author: DKS
     * @since: 2021/11/10 14:29
     * @Param: []
     * @return: java.util.Map
     */
    Map getRefundConfig();
    
    /**
     * @Description: 查询支付配置状态
     * @author: DKS
     * @since: 2021/11/10 14:31
     * @Param: []
     * @return: java.util.Map
     */
    Map getBasicConfig();
}
