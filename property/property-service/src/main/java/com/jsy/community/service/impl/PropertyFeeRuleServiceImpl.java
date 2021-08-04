package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.FeeRuleVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 小区物业收费规则
 * @author: Hu
 * @create: 2021-04-20 16:30
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyFeeRuleServiceImpl extends ServiceImpl<PropertyFeeRuleMapper, PropertyFeeRuleEntity> implements IPropertyFeeRuleService {
    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;



    /**
     * @Description: 新增缴费规则
     * @author: Hu
     * @since: 2021/7/20 14:26
     * @Param: [communityId, propertyFeeRuleEntity]
     * @return: void
     */
    @Override
    public void saveOne(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
//        propertyFeeRuleEntity.setCommunityId(userInfo.getCommunityId());
        propertyFeeRuleEntity.setCreateBy(userInfo.getUid());
        propertyFeeRuleEntity.setStatus(0);
        propertyFeeRuleEntity.setCreateTime(LocalDateTime.now());
        Integer size = propertyFeeRuleMapper.selectCount(new QueryWrapper<PropertyFeeRuleEntity>().eq("community_id", userInfo.getCommunityId()));
        size++;
        String value = String.valueOf(size);
        if (value.length()==1){
            propertyFeeRuleEntity.setSerialNumber("000"+value);
        }else {
            if(value.length()==2){
                propertyFeeRuleEntity.setSerialNumber("00"+value);
            }else {
                if (value.length()==3){
                    propertyFeeRuleEntity.setSerialNumber("0"+value);
                }else {
                    propertyFeeRuleEntity.setSerialNumber(value);
                }
            }
        }
        propertyFeeRuleMapper.insert(propertyFeeRuleEntity);
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [userInfo, propertyFeeRuleEntity]
     * @return: void
     */
    @Override
    public void updateOneRule(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
        propertyFeeRuleEntity.setUpdateBy(userInfo.getUid());
        propertyFeeRuleMapper.updateById(propertyFeeRuleEntity);
    }


    /**
     * @Description: 启用或者停用
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [userInfo, status, id]
     * @return: void
     */
    @Override
    public void startOrOut(AdminInfoVo userInfo, Integer status,Long id) {
        PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectById(id);
        if (status==1){
            PropertyFeeRuleEntity ruleEntity = propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("type", entity.getType()).eq("status", 1).eq("community_id",entity.getCommunityId()));
            if (ruleEntity!=null){
                ruleEntity.setStatus(0);
                ruleEntity.setUpdateBy(userInfo.getUid());
                propertyFeeRuleMapper.updateById(ruleEntity);
            }
            entity.setUpdateBy(userInfo.getUid());
            entity.setStatus(1);
            propertyFeeRuleMapper.updateById(entity);
        }else {
            entity.setUpdateBy(userInfo.getUid());
            entity.setStatus(0);
            propertyFeeRuleMapper.updateById(entity);
        }
    }


    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [communityId, type]
     * @return: com.jsy.community.entity.property.PropertyFeeRuleEntity
     */
    @Override
    public PropertyFeeRuleEntity selectByOne(Long id) {
        return propertyFeeRuleMapper.selectById(id);
    }


    /**
     * @Description: 查询当前小区收费规则
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [baseQO, communityId]
     * @return: java.util.Map<java.lang.Object,java.lang.Object>
     */
    @Override
    public Map<Object, Object> findList(BaseQO<FeeRuleQO> baseQO,Long communityId) {
        FeeRuleQO query = baseQO.getQuery();
        if (baseQO.getSize()==null||baseQO.getSize()<=0){
            baseQO.setSize(10L);
        }
        QueryWrapper<PropertyFeeRuleEntity> wrapper=new QueryWrapper<PropertyFeeRuleEntity>();
        List<FeeRuleVO> page = propertyFeeRuleMapper.findList((baseQO.getPage()-1)*baseQO.getSize(),baseQO.getSize(),baseQO.getQuery());
        for (FeeRuleVO feeRuleVO : page) {
            feeRuleVO.setPeriodName(BusinessEnum.FeeRulePeriodEnum.getName(feeRuleVO.getPeriod()));
            feeRuleVO.setChargeModeName(feeRuleVO.getChargeMode()==1?"面积":"固定金额");
            if (feeRuleVO.getChargeMode()==1){
                if (feeRuleVO.getDisposable()==1){
                    feeRuleVO.setFormulaName("单价*面积");
                }else {
                    feeRuleVO.setFormulaName("单价*面积*周期");
                }
            }else {
                if (feeRuleVO.getDisposable()==1){
                    feeRuleVO.setFormulaName("固定金额");
                }else {
                    feeRuleVO.setFormulaName("单价*周期");
                }
            }
        }
        Integer total = propertyFeeRuleMapper.findTotal(baseQO.getQuery());
        Map<Object, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",page);
        return map;
    }
}
