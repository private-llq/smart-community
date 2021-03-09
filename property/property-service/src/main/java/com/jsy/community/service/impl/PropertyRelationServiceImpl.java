package com.jsy.community.service.impl;

import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.PropertyRelationMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-05 11:22
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyRelationServiceImpl implements IPropertyRelationService {
    @Autowired
    private PropertyRelationMapper propertyRelationMapper;


    @Override
    public List list(BaseQO<PropertyRelationQO> baseQO) {
        return null;
    }
}
