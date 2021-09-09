package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.PropertyRelationMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseMemberQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import com.jsy.community.vo.PropertyRelationVO;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.HouseMemberVO;
import com.jsy.community.vo.property.RelationImportErrVO;
import com.jsy.community.vo.property.RelationImportQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
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

    @Autowired
    private HouseMapper houseMapper;



    /**
     * @Description: 房屋下拉框
     * @author: Hu
     * @since: 2021/5/21 11:09
     * @Param: [baseQO, adminInfoVo]
     * @return: java.util.List<com.jsy.community.vo.HouseTypeVo>
     */
    @Override
    public List<FeeRelevanceTypeVo> getHouseId(BaseQO<RelationListQO> baseQO, AdminInfoVo adminInfoVo) {
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
            relationVO.setRelationName(BusinessEnum.RelationshipEnum.getCodeName(relationVO.getRelation()));
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
    public void save(HouseMemberEntity houseMemberEntity,String uid) {
//        houseMemberEntity.setStatus(1);
        houseMemberEntity.setId(SnowFlake.nextId());
        houseMemberEntity.setHouseholderId(uid);
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
        HouseMemberEntity memberEntity = propertyRelationMapper.selectById(id);
        HouseEntity entity = houseMapper.selectById(memberEntity.getHouseId());
        memberEntity.setHouseSite(entity.getBuilding()+entity.getUnit()+entity.getDoor());
        memberEntity.setRelationName(BusinessEnum.RelationshipEnum.getCodeName(memberEntity.getRelation()));
        return memberEntity;
    }


    /**
     * @Description: 迁入
     * @author: Hu
     * @since: 2021/7/23 17:26
     * @Param: [id]
     * @return: void
     */
    @Override
    public void immigration(Long id) {
        HouseMemberEntity entity = propertyRelationMapper.selectById(id);
        if (entity!=null){
            entity.setStatus(1);
        }
        propertyRelationMapper.updateById(entity);
    }


    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/7/23 17:55
     * @Param: [baseQO]
     * @return: void
     */
    @Override
    public Map<String, Object> pageList(BaseQO<HouseMemberQO> baseQO) {
        Map<String, Object> map = new HashMap<>();
        if (baseQO.getPage()==null&&baseQO.getPage()==0)
        {
            baseQO.setPage(1L);
        }

        List<HouseMemberVO> list=propertyRelationMapper.pageList((baseQO.getPage()-1) * baseQO.getSize(),baseQO.getSize(),baseQO.getQuery());
        for (HouseMemberVO houseMemberVO : list) {
            houseMemberVO.setRelationName(BusinessEnum.RelationshipEnum.getCodeName(houseMemberVO.getRelation()));
        }
        Long total = propertyRelationMapper.pageListTotal(baseQO.getQuery());
        map.put("list",list);
        map.put("total",total);
        return map;
    }

    /**
     * @Description: 迁出
     * @author: Hu
     * @since: 2021/7/23 17:26
     * @Param: [id]
     * @return: void
     */
    @Override
    public void emigration(Long id) {
        HouseMemberEntity entity = propertyRelationMapper.selectById(id);
        if (entity!=null){
            entity.setStatus(2);
        }
        propertyRelationMapper.updateById(entity);
    }



    /**
     * @Description: 导入数据库返回其中错误信息
     * @author: Hu
     * @since: 2021/9/4 9:52
     * @Param: [list, communityId, uid]
     * @return: java.util.List<com.jsy.community.vo.property.RelationImportErrVO>
     */
    @Override
    public List<RelationImportErrVO> importRelation(List<RelationImportQO> list, Long communityId, String uid) {
        List<RelationImportErrVO> errVOList = new LinkedList<>();
        List<HouseMemberEntity> entityList = new LinkedList<>();
        HouseMemberEntity memberEntity = null;
        RelationImportErrVO errVO = null;
        for (RelationImportQO relationImportQO : list) {
            //查询房屋
            HouseEntity houseEntity = houseMapper.selectOne(new QueryWrapper<HouseEntity>()
                    .eq("community_id", communityId)
                    .eq("building", relationImportQO.getBuilding())
                    .eq("unit", relationImportQO.getUnit())
                    .eq("door", relationImportQO.getDoor())
                    .eq("type",4));
            if (Objects.isNull(houseEntity)){
                errVO = new RelationImportErrVO();
                BeanUtils.copyProperties(relationImportQO,errVO);
                errVO.setError("房屋错误:请填写正确的房屋");
                if (BusinessEnum.RelationshipEnum.getNameCode(relationImportQO.getRelation())==null){
                    errVO.setError(errVO.getError()+",身份不正确！");
                }
                errVOList.add(errVO);
            } else {
                if (BusinessEnum.RelationshipEnum.getNameCode(relationImportQO.getRelation())==null){
                    errVO=new RelationImportErrVO();
                    BeanUtils.copyProperties(relationImportQO,errVO);
                    errVO.setError("身份不正确！");
                    errVOList.add(errVO);
                    continue;
                }
                memberEntity = new HouseMemberEntity();
                BeanUtils.copyProperties(relationImportQO,memberEntity);
                memberEntity.setId(SnowFlake.nextId());
                memberEntity.setHouseId(houseEntity.getId());
                memberEntity.setRelation(BusinessEnum.RelationshipEnum.getNameCode(relationImportQO.getRelation()));
                memberEntity.setCommunityId(communityId);
                memberEntity.setUnit(relationImportQO.getUnit());
                entityList.add(memberEntity);
            }
        }
        if (entityList.size()!=0){
            propertyRelationMapper.saveList(entityList);
        }
        return errVOList;
    }

    /**
     * @Description: 导出成员信息表
     * @author: Hu
     * @since: 2021/8/31 15:37
     * @Param: [houseMemberQO]
     * @return: java.util.List<com.jsy.community.entity.HouseEntity>
     */
    @Override
    public List<HouseMemberVO> queryExportRelationExcel(HouseMemberQO houseMemberQO) {
        return propertyRelationMapper.queryExportRelationExcel(houseMemberQO);
    }

    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/8/4 9:15
     * @Param:
     * @return:
     */
    @Override
    public void delete(Long id) {
        propertyRelationMapper.deleteById(id);
    }

    /**
     * @Description: 批量删除
     * @author: Hu
     * @since: 2021/8/3 17:20
     * @Param: [longAry]
     * @return: void
     */
    @Override
    public void deletes(Long[] longAry) {
        propertyRelationMapper.deletes(longAry);
    }

    @Override
    public void emigrations(Long[] ids) {
        propertyRelationMapper.emigrations(ids);
    }
}
