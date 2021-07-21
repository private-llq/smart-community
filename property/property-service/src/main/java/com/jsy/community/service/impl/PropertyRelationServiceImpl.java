package com.jsy.community.service.impl;

import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
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
 * @description:  物业成员查询接口
 * @author: Hu
 * @create: 2021-03-05 11:22
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyRelationServiceImpl implements IPropertyRelationService {
    @Autowired
    private PropertyRelationMapper propertyRelationMapper;



    /**
     * @Description: 房屋下拉框
     * @author: Hu
     * @since: 2021/5/21 11:09
     * @Param: [baseQO, adminInfoVo]
     * @return: java.util.List<com.jsy.community.vo.HouseTypeVo>
     */
    @Override
    public List<HouseTypeVo> getHouseId(BaseQO<RelationListQO> baseQO,AdminInfoVo adminInfoVo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        if ("".equals(baseQO.getPage())||baseQO.getPage()==0) {
            baseQO.setPage(1L);
        }
        RelationListQO qoQuery = baseQO.getQuery();
        qoQuery.setUid(adminInfoVo.getUid());
        qoQuery.setCommunityId(adminInfoVo.getCommunityId());
        Long page=(baseQO.getPage()-1)*baseQO.getSize();
        return propertyRelationMapper.getHouseId(qoQuery,page,baseQO.getSize());
    }


    /**
     * @Description: 楼栋下拉框
     * @author: Hu
     * @since: 2021/5/21 11:10
     * @Param: [baseQO, adminInfoVo]
     * @return: java.util.List
     */
    @Override
    public List getBuildingId(BaseQO<RelationListQO> baseQO,AdminInfoVo adminInfoVo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        if ("".equals(baseQO.getPage())||baseQO.getPage()==0) {
            baseQO.setPage(1l);
        }
        RelationListQO qoQuery = baseQO.getQuery();
        qoQuery.setUid(adminInfoVo.getUid());
        qoQuery.setCommunityId(adminInfoVo.getCommunityId());
        Long page=(baseQO.getPage()-1)*baseQO.getSize();
        return propertyRelationMapper.getBuildingId(qoQuery,page,baseQO.getSize());
    }


    /**
     * @Description: 单元下拉框
     * @author: Hu
     * @since: 2021/5/21 11:10
     * @Param: [baseQO, adminInfoVo]
     * @return: java.util.List
     */
    @Override
    public List getUnitId(BaseQO<RelationListQO> baseQO, AdminInfoVo adminInfoVo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        if ("".equals(baseQO.getPage())||baseQO.getPage()==0) {
            baseQO.setPage(1l);
        }
        RelationListQO qoQuery = baseQO.getQuery();
        qoQuery.setUid(adminInfoVo.getUid());
        qoQuery.setCommunityId(adminInfoVo.getCommunityId());
        Long page=(baseQO.getPage()-1)*baseQO.getSize();
        return propertyRelationMapper.getUnitId(qoQuery,page,baseQO.getSize());
    }


    /**
     * @Description: 成员列表
     * @author: Hu
     * @since: 2021/5/21 11:10
     * @Param: [baseQO]
     * @return: java.util.Map
     */
    @Override
    public Map list(BaseQO<PropertyRelationQO> baseQO,Long communityId) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        if ("".equals(baseQO.getPage())||baseQO.getPage()==0) {
            baseQO.setPage(1l);
        }
        Long page=(baseQO.getPage()-1)*baseQO.getSize();
        PropertyRelationQO qoQuery = baseQO.getQuery();
        qoQuery.setCommunityId(communityId);
        List<PropertyRelationVO> relationVOS = propertyRelationMapper.list(qoQuery, page, baseQO.getSize());
        for (PropertyRelationVO relationVO : relationVOS) {
            relationVO.setRelationName(BusinessEnum.RelationshipEnum.getCode(relationVO.getRelation()));
            relationVO.setHousing(replaceStr(relationVO.getHousing()));
            relationVO.setHouseTypeName(relationVO.getHouseType()==1?"商铺":relationVO.getHouseType()==2?"住宅":"");
        }

        Map map = new HashMap<>();
        map.put("list",relationVOS);
        map.put("total",propertyRelationMapper.getTotal(qoQuery));
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



    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/7/21 16:36
     * @Param: [houseMemberEntity]
     * @return: void
     */
    @Override
    public void save(HouseMemberEntity houseMemberEntity) {
        propertyRelationMapper.insert(houseMemberEntity);
    }


    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/7/21 16:36
     * @Param: [houseMemberEntity]
     * @return: void
     */
    @Override
    public void update(HouseMemberEntity houseMemberEntity) {
        propertyRelationMapper.updateById(houseMemberEntity);
    }


    /**
     * @Description: 单查
     * @author: Hu
     * @since: 2021/7/21 16:36
     * @Param: [id]
     * @return: com.jsy.community.entity.HouseMemberEntity
     */
    @Override
    public HouseMemberEntity findOne(Long id) {
        return propertyRelationMapper.selectById(id);
    }
}
