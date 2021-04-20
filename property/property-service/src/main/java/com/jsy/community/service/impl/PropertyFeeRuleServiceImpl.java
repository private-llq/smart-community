package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.property.CommunityPropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.CommunityPropertyFeeRuleMapper;
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
    private CommunityPropertyFeeRuleMapper communityRuleMapper;
    @Autowired
    private CommunityMapper communityMapper;

    @Override
    @Transactional
    public void updateAll() {
        List<CommunityEntity> list = communityMapper.selectList(null);
        for (CommunityEntity communityEntity : list) {
            Integer i=1;
            PropertyFeeRuleEntity ruleEntity = new PropertyFeeRuleEntity();
            ruleEntity.setId(SnowFlake.nextId());
            ruleEntity.setType(1);
            String str=i+"";
            ruleEntity.setSerialNumber(str.length()==1?"000"+i:str.length()==2?"00"+i:"0"+i);
            ruleEntity.setName("物业费");
            ruleEntity.setPeriod(3);
            ruleEntity.setChargeMode(1);
            ruleEntity.setMonetaryUnit(new BigDecimal(300));
            ruleEntity.setPenalSum(new BigDecimal(0.01));
            ruleEntity.setPenalDays(3);
            ruleEntity.setStatus(0);
            ruleEntity.setCreateBy("1a7a182d711e441fbb24659090daf5cb");
            ruleEntity.setUpdateBy("");
            propertyFeeRuleMapper.insert(ruleEntity);
            CommunityPropertyFeeRuleEntity entity = new CommunityPropertyFeeRuleEntity();
            entity.setCommunityId(communityEntity.getId());
            entity.setRuleId(ruleEntity.getId());
            communityRuleMapper.insert(entity);
            i++;
        }
    }

    @Override
    public List<PropertyFeeRuleEntity> findList(String communityId) {
        return propertyFeeRuleMapper.findList(communityId);
    }
}
