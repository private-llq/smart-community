package com.jsy.community.service.impl;

import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.PropertyRelationMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.vo.HouseTypeVo;
import com.jsy.community.vo.PropertyRelationVO;
import com.jsy.community.vo.admin.AdminInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public List<HouseTypeVo> getHouseId(BaseQO<RelationListQO> baseQO,AdminInfoVo adminInfoVo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        RelationListQO qoQuery = baseQO.getQuery();
        qoQuery.setUid(adminInfoVo.getUid());
        qoQuery.setCommunityId(adminInfoVo.getCommunityId());
        return propertyRelationMapper.getHouseId(qoQuery,baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public List getBuildingId(BaseQO<RelationListQO> baseQO,AdminInfoVo adminInfoVo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        RelationListQO qoQuery = baseQO.getQuery();
        qoQuery.setUid(adminInfoVo.getUid());
        qoQuery.setCommunityId(adminInfoVo.getCommunityId());
        return propertyRelationMapper.getBuildingId(qoQuery,baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public List getUnitId(BaseQO<RelationListQO> baseQO, AdminInfoVo adminInfoVo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        RelationListQO qoQuery = baseQO.getQuery();
        qoQuery.setUid(adminInfoVo.getUid());
        qoQuery.setCommunityId(adminInfoVo.getCommunityId());
        return propertyRelationMapper.getUnitId(qoQuery,baseQO.getPage(),baseQO.getSize());
    }

    @Override
    public Map list(BaseQO<PropertyRelationQO> baseQO) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        List<PropertyRelationVO> relationVOS = propertyRelationMapper.list(baseQO.getQuery(), baseQO.getPage(), baseQO.getSize());
        for (PropertyRelationVO relationVO : relationVOS) {
            relationVO.setRelationName(BusinessEnum.RelationshipEnum.getCode(relationVO.getRelation()));
            relationVO.setHousing(replaceStr(relationVO.getHousing()));
            relationVO.setHouseTypeName(relationVO.getHouseType()==1?"商铺":relationVO.getHouseType()==2?"住宅":"");
        }

        Map map = new HashMap<>();
        map.put("list",relationVOS);
        map.put("total",propertyRelationMapper.getTotal(baseQO.getQuery(),baseQO.getPage(),baseQO.getSize()));
        return map;
    }
    public static String replaceStr(String str){
        StringBuffer buf = new StringBuffer();
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        while(m.find()){
            String chinese = m.group();//匹配出的中文
            String pinyin = "";//在你的中文与拼音对应中找到对应拼音。
            m.appendReplacement(buf, pinyin);
        }
        return m.appendTail(buf).toString();
    }
}
