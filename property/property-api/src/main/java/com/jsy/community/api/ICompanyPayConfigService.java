package com.jsy.community.api;

import com.jsy.community.entity.CompanyPayConfigEntity;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 物业公司支付配置
 * @author: Hu
 * @create: 2021-09-10 14:25
 **/
public interface ICompanyPayConfigService {

    /**
     * @Description: 更新基本配置
     * @author: Hu
     * @since: 2021/9/10 16:32
     * @Param:
     * @return:
     */
    void basicConfig(CompanyPayConfigEntity communityHardWareEntity, Long companyId);

    /**
     * @Description: 查询支付公私钥状态
     * @author: Hu
     * @since: 2021/9/10 16:57
     * @Param:
     * @return:
     */
    CompanyPayConfigEntity getConfig(Long companyId);

    /**
     * @Description: 查询小区支付配置
     * @author: Hu
     * @since: 2021/9/13 11:34
     * @Param:
     * @return:
     */
    CompanyPayConfigEntity getCompanyConfig(Long propertyId);

    /**
     * @Description: 查询退款配置
     * @author: Hu
     * @since: 2021/9/13 14:48
     * @Param:
     * @return:
     */
    Map getRefundConfig(Long companyId);
    
    /**
     * @Description: 查询支付配置状态
     * @author: DKS
     * @since: 2021/9/13 15:04
     * @Param: companyId
     * @return: java.util.Map
     */
    Map getBasicConfig(Long companyId);
}
