package com.jsy.community.api;

import com.jsy.community.entity.CompanyPayConfigEntity;

/**
 * @program: com.jsy.community
 * @description: 物业公司支付配置
 * @author: Hu
 * @create: 2021-09-10 14:25
 **/
public interface ICompanyPayConfigService {
    /**
     * @Description: 更新私钥公钥
     * @author: Hu
     * @since: 2021/9/10 16:03
     * @Param:
     * @return:
     */
    void configUpdate(CompanyPayConfigEntity communityHardWareEntity,Long companyId);

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
}
