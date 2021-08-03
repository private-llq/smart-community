package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleConstService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFeeRuleConstEntity;
import com.jsy.community.mapper.PropertyFeeRuleConstMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业缴费项目公共常量
 * @author: Hu
 * @create: 2021-07-30 14:27
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyFeeRuleConstServiceImpl extends ServiceImpl<PropertyFeeRuleConstMapper, PropertyFeeRuleConstEntity> implements IPropertyFeeRuleConstService {

    @Autowired
    private PropertyFeeRuleConstMapper propertyFeeRuleConstMapper;



    public LinkedList<PropertyFeeRuleConstEntity> listAll(){
        LinkedList<PropertyFeeRuleConstEntity> list = new LinkedList<>();
        List<PropertyFeeRuleConstEntity> entityList = propertyFeeRuleConstMapper.selectList(null);
        for (PropertyFeeRuleConstEntity entity : entityList) {
            if (entity.getPid()==null){
                list.add(entity);
            }
        }
        entityList.removeAll(list);
        for (PropertyFeeRuleConstEntity constEntity : list) {
            constEntity.setEntityList(getMenu(constEntity,entityList));
        }
        return list;
    }

    public List getMenu(PropertyFeeRuleConstEntity constEntity,List<PropertyFeeRuleConstEntity> entityList){
        LinkedList<PropertyFeeRuleConstEntity> list = new LinkedList<>();
        for (PropertyFeeRuleConstEntity entity : entityList) {
            if (entity.getPid()==constEntity.getId()){
                list.add(entity);
            }
        }
        for (PropertyFeeRuleConstEntity propertyFeeRuleConstEntity : list) {
            propertyFeeRuleConstEntity.setEntityList(getMenu(propertyFeeRuleConstEntity,entityList));
        }
        return list;
    }
}
