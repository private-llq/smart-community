package com.jsy.community.api;

import com.jsy.community.entity.PropertyCompanyEntity;

/**
 * @program: com.jsy.community
 * @description: 物业公司
 * @author: Hu
 * @create: 2021-08-20 15:05
 **/
public interface IPropertyCompanyService {
    /**
     * @Description: 当前小区所属的物业公司
     * @author: Hu
     * @since: 2021/8/20 15:08
     * @Param:
     * @return:
     */
    PropertyCompanyEntity findOne(Long id);
    
    /**
     * @author: DKS
     * @description: 根据物业公司id获取物业公司名称
     * @param companyId :
     * @return: com.jsy.community.entity.PropertyCompanyEntity
     * @date: 2021/8/25 16:32
     **/
    String getCompanyNameByCompanyId(Long companyId);
    
    /**
     * @author: DKS
     * @description: 物业端-系统设置-短信配置
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/1 11:50
     **/
    Boolean updateSMSConfig(PropertyCompanyEntity propertyCompanyEntity);
    
    /**
     * @author: DKS
     * @description: 通过物业公司id查询物业公司详情
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/1 11:50
     **/
    PropertyCompanyEntity selectCompany(Long companyId);
}
