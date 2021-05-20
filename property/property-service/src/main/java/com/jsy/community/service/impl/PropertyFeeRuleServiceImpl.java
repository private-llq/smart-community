package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public void updateOneRule(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
        propertyFeeRuleEntity.setUpdateBy(userInfo.getUid());
        propertyFeeRuleMapper.updateById(propertyFeeRuleEntity);
    }

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

    @Override
    public PropertyFeeRuleEntity selectByOne(Long communityId, Integer type) {
        return propertyFeeRuleMapper.selectOne(new QueryWrapper<PropertyFeeRuleEntity>().eq("type",type).eq("community_id",communityId));
    }

    @Override
    public Map<Object, Object> findList(BaseQO<FeeRuleQO> baseQO,Long communityId) {
        FeeRuleQO query = baseQO.getQuery();
        if (baseQO.getSize()==null||baseQO.getSize()<=0){
            baseQO.setSize(10L);
        }
        QueryWrapper<PropertyFeeRuleEntity> wrapper=new QueryWrapper<PropertyFeeRuleEntity>();
        wrapper.eq("community_id", communityId);
        if (!"".equals(query.getKey())&&query.getKey()!=null){
            wrapper.like("name", query.getKey()).or().like("serial_number", query.getKey());
        }
        Page<PropertyFeeRuleEntity> page = propertyFeeRuleMapper.selectPage(new Page<>(baseQO.getPage(), baseQO.getSize()), wrapper);
        List<PropertyFeeRuleEntity> pageRecords = page.getRecords();
        for (PropertyFeeRuleEntity entity : pageRecords) {
            if (!"".equals(entity.getUpdateBy())&&entity.getUpdateBy()!=null){
                AdminUserEntity userEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("uid", entity.getUpdateBy()));
                entity.setUpdateByName(userEntity.getRealName());
            }
        }
        Map<Object, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("list",pageRecords);
        return map;
    }
}
