package com.jsy.community.api;

import com.jsy.community.entity.property.PropertyFeeRuleConstEntity;

import java.util.LinkedList;

/**
 * @program: com.jsy.community
 * @description: 物业缴费项目公共常量
 * @author: Hu
 * @create: 2021-07-30 16:35
 **/
public interface IPropertyFeeRuleConstService {

    /**
     * @Description: 查询缴费项目公共常量
     * @author: Hu
     * @since: 2021/7/30 16:37
     * @Param:
     * @return:
     */
    LinkedList<PropertyFeeRuleConstEntity> listAll();
}
