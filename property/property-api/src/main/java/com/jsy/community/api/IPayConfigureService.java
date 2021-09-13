package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PayConfigureEntity;

/**
 * @program: com.jsy.community
 * @description: 支付配置
 * @author: DKS
 * @create: 2021-09-09 09:47
 **/
public interface IPayConfigureService extends IService<PayConfigureEntity> {
    /**
     * @Description: 更新配置
     * @author: DKS
     * @since: 2021/9/13 14:17
     * @Param: [payConfigureEntity, companyId]
     * @return: void
     */
    void basicConfig(PayConfigureEntity payConfigureEntity, Long companyId);
    
    /**
     * @Description: 查询支付证书状态
     * @author: DKS
     * @since: 2021/9/13 14:41
     * @Param: [companyId]
     * @return: com.jsy.community.entity.PayConfigureEntity
     */
    PayConfigureEntity getConfig(Long companyId);
}
