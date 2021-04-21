package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  小区物业收费规则
 * @author: Hu
 * @create: 2021-04-20 16:30
 **/
public interface IPropertyFeeRuleService extends IService<PropertyFeeRuleEntity> {
    /**
     * @Description: 查询当前小区收费规则
     * @author: Hu
     * @since: 2021/4/21 17:08
     * @Param:
     * @return:
     */
    List<PropertyFeeRuleEntity> findList(String communityId);

}
