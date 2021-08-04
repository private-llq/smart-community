package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.vo.admin.AdminInfoVo;

import java.util.Map;

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
    Map<Object, Object> findList(BaseQO<FeeRuleQO> baseQO, Long communityId);

    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/4/22 16:26
     * @Param:
     * @return:
     */
    PropertyFeeRuleEntity selectByOne(Long id);

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

    /**
     * @Description: 新增缴费规则
     * @author: Hu
     * @since: 2021/7/20 14:26
     * @Param:
     * @return:
     */
    void saveOne(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity);
}
