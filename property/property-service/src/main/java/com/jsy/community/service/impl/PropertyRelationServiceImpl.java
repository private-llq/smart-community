package com.jsy.community.service.impl;

import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.PropertyRelationMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<HouseTypeVo> getHouseId(BaseQO<RelationListQO> baseQO) {
        return propertyRelationMapper.getHouseId(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public List getBuildingId(BaseQO<RelationListQO> baseQO) {
        return propertyRelationMapper.getBuildingId(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public List getUnitId(BaseQO<RelationListQO> baseQO) {
        return propertyRelationMapper.getUnitId(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public Map list(BaseQO<PropertyRelationQO> baseQO) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        Map map = new HashMap<>();
        map.put("list",propertyRelationMapper.list(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize()));
        map.put("total",propertyRelationMapper.getTotal(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize()));
        return map;
    }
}
