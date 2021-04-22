package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public PropertyFeeRuleEntity selectOneById(Long id) {
        return propertyFeeRuleMapper.selectById(id);
    }

    @Override
    public void updateOneRule(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
        propertyFeeRuleEntity.setUpdateBy(userInfo.getUid());
        propertyFeeRuleMapper.updateById(propertyFeeRuleEntity);
    }

    @Override
    public void startOrOut(AdminInfoVo userInfo, Integer status,Long id) {
        PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectById(id);
        if (status==1){
            entity.setUpdateBy(userInfo.getUid());
            entity.setStatus(1);
            propertyFeeRuleMapper.updateById(entity);
        }else {
            entity.setUpdateBy(userInfo.getUid());
            entity.setStatus(0);
            propertyFeeRuleMapper.updateById(entity);
        }
    }

    @Override
    public PropertyFeeRuleEntity selectOne(Long communityId, Integer type) {
        return propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("type",type).eq("community_id",communityId));
    }

    @Override
    public List<PropertyFeeRuleEntity> findList(Long communityId) {
        List<PropertyFeeRuleEntity> entities = propertyFeeRuleMapper.selectList(new QueryWrapper<PropertyFeeRuleEntity>().eq("community_id", communityId));
        for (PropertyFeeRuleEntity entity : entities) {
            if (entity.getUpdateBy()!=null){
                AdminUserEntity userEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("uid", entity.getUpdateBy()));
                entity.setUpdateByName(userEntity.getRealName());
            }
        }
        return entities;
    }
}
