package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private CommunityMapper communityMapper;

    @Override
    public List<PropertyFeeRuleEntity> findList(String communityId) {
        return propertyFeeRuleMapper.findList(communityId);
    }
}
