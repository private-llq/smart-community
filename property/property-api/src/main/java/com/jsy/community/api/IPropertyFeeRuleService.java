package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.vo.admin.AdminInfoVo;

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
    List<PropertyFeeRuleEntity> findList(Long communityId);

    PropertyFeeRuleEntity selectOne(Long communityId, Integer type);

    /**
     * @Description: 启用或者停用
     * @author: Hu
     * @since: 2021/4/22 14:28
     * @Param:
     * @return:
     */
    void startOrOut(AdminInfoVo userInfo, Integer status,Long id);

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/4/22 15:28
     * @Param:
     * @return:
     */
    void updateOneRule(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity);

}
