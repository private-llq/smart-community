package com.jsy.community.service.impl;

import com.jsy.community.api.ITenementService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.TenementMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import com.jsy.community.vo.PropertyTenementVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-10 14:36
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class TenementServiceImpl implements ITenementService {
    @Autowired
    private TenementMapper tenementMapper;


    @Override
    public List<HouseTypeVo> getHouseId(BaseQO<RelationListQO> baseQO) {
        return tenementMapper.getHouseId(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public List getBuildingId(BaseQO<RelationListQO> baseQO) {
        return tenementMapper.getBuildingId(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public List getUnitId(BaseQO<RelationListQO> baseQO) {
        return tenementMapper.getUnitId(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public Map list(BaseQO<PropertyRelationQO> baseQO) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        List<PropertyTenementVO> relationVOS = tenementMapper.list(baseQO.getQuery(), baseQO.getPage(), baseQO.getSize());
        Map map = new HashMap<>();
        map.put("list",relationVOS);
        map.put("total",tenementMapper.getTotal(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize()));
        return map;
    }
}
