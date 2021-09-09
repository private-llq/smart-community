package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.qo.property.FeeRuleRelevanceQO;
import com.jsy.community.qo.property.UpdateRelevanceQO;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import com.jsy.community.vo.admin.AdminInfoVo;

import java.util.List;
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

    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/9/1 9:50
     * @Param:
     * @return:
     */
    void delete(Long id);

    /**
     * @Description: 启用和停用报表状态
     * @author: Hu
     * @since: 2021/9/1 9:52
     * @Param:
     * @return:
     */
    void statementStatus(AdminInfoVo userInfo, Integer status, Long id);

    /**
     * @Description: 删除缴费项目的车位或者房屋
     * @author: Hu
     * @since: 2021/9/6 13:56
     * @Param:
     * @return:
     */
    void deleteRelevance(Long id);

    /**
     * @Description: 添加收费项目关联目标
     * @author: Hu
     * @since: 2021/9/6 14:03
     * @Param:
     * @return:
     */
    void addRelevance(UpdateRelevanceQO updateRelevanceQO);

    /**
     * @Description: 查询收费项目关联目标
     * @author: Hu
     * @since: 2021/9/6 14:14
     * @Param:
     * @return:
     */
    List selectRelevance(FeeRuleRelevanceQO feeRuleRelevanceQO);

    /**
     *@Author: DKS
     *@Description: 根据收费项目名称查询收费项目id
     *@Param: excel:
     *@Date: 2021/9/7 15:27
     **/
    Long selectFeeRuleIdByFeeRuleName(String feeRuleName, Long communityId);

    /**
     * @Description: 查询当前小区业主认证过的房屋
     * @author: Hu
     * @since: 2021/9/7 11:08
     * @Param:
     * @return:
     */
    List<FeeRelevanceTypeVo> getHouse(Long communityId);

    /**
     * @Description: 查询当前小区的月租或属于业主的车位
     * @author: Hu
     * @since: 2021/9/7 11:11
     * @Param:
     * @return:
     */
    List<FeeRelevanceTypeVo> getCarPosition(Long adminCommunityId, Integer type);
}
