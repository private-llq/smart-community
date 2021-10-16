package com.jsy.community.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IPropertyRelationService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.*;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ProprietorMapper proprietorMapper;

    @Autowired
    private PropertyUserHouseMapper propertyUserHouseMapper;

    @Autowired
    private UserMapper userMapper;



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
    @Transactional
    public void save(HouseMemberEntity houseMemberEntity,String uid) {
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", houseMemberEntity.getMobile()));
        if (userEntity!=null){
            houseMemberEntity.setUid(userEntity.getUid());
        }
        houseMemberEntity.setId(SnowFlake.nextId());
        propertyRelationMapper.insert(houseMemberEntity);

        if (houseMemberEntity.getRelation()==1){
            ProprietorEntity entity = new ProprietorEntity();
            BeanUtils.copyProperties(houseMemberEntity,entity);
            entity.setRealName(houseMemberEntity.getName());
            entity.setMobile(houseMemberEntity.getMobile());
            entity.setId(SnowFlake.nextId());
            proprietorMapper.insert(entity);

            UserHouseEntity userHouseEntity = new UserHouseEntity();
            userHouseEntity.setCommunityId(houseMemberEntity.getCommunityId());
            userHouseEntity.setHouseId(houseMemberEntity.getHouseId());
            userHouseEntity.setCheckStatus(1);
            userHouseEntity.setId(SnowFlake.nextId());
            propertyUserHouseMapper.insert(userHouseEntity);
        }
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
        HouseMemberEntity memberEntity = propertyRelationMapper.selectById(houseMemberEntity.getId());
        if (memberEntity.getRelation()==1){
            ProprietorEntity entity = proprietorMapper.selectOne(new QueryWrapper<ProprietorEntity>().eq("house_id", memberEntity.getHouseId())
                    .eq("community_id", memberEntity.getCommunityId()).eq("real_name", memberEntity.getName()).eq("mobile", memberEntity.getMobile()));
            if (entity!=null){
                if (houseMemberEntity.getRelation()!=1){
                    proprietorMapper.deleteById(entity.getId());
                }else {
                    entity.setRealName(houseMemberEntity.getName());
                    entity.setMobile(houseMemberEntity.getMobile());
                    entity.setHouseId(houseMemberEntity.getHouseId());
                    entity.setCommunityId(houseMemberEntity.getCommunityId());
                    proprietorMapper.updateById(entity);
                }
            }
        }else if (houseMemberEntity.getRelation()==1){
            ProprietorEntity proprietorEntity = new ProprietorEntity();
            BeanUtils.copyProperties(houseMemberEntity,proprietorEntity);
            proprietorEntity.setId(SnowFlake.nextId());
            proprietorEntity.setRealName(houseMemberEntity.getName());
            proprietorMapper.insert(proprietorEntity);
        }

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
    @Transactional
    public List<RelationImportErrVO> importRelation(List<RelationImportQO> list, Long communityId, String uid) {
        List<RelationImportErrVO> errVOList = new LinkedList<>();
        List<HouseMemberEntity> entityList = new LinkedList<>();
        List<ProprietorEntity> entities = new LinkedList<>();
        List<UserHouseEntity> userHouseList = new LinkedList<>();
        UserHouseEntity userHouseEntity = null;
        ProprietorEntity proprietorEntity = null;
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

                if (memberEntity.getRelation()==1){
                    proprietorEntity = new ProprietorEntity();
                    BeanUtils.copyProperties(memberEntity,proprietorEntity);
                    proprietorEntity.setId(SnowFlake.nextId());
                    proprietorEntity.setRealName(memberEntity.getName());
                    entities.add(proprietorEntity);

                    UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", memberEntity.getMobile()));
                    userHouseEntity = new UserHouseEntity();
                    if (userEntity != null) {
                        userHouseEntity.setUid(userEntity.getUid());
                    }
                    userHouseEntity.setHouseId(memberEntity.getHouseId());
                    userHouseEntity.setId(SnowFlake.nextId());
                    userHouseEntity.setCommunityId(memberEntity.getCommunityId());
                    userHouseEntity.setCheckStatus(1);
                    userHouseList.add(userHouseEntity);
                }
            }
        }
        if (entityList.size()!=0){
            propertyRelationMapper.saveList(entityList);
        }
        if (entities.size()!=0){
            proprietorMapper.saveList(entities);
        }
        if (userHouseList.size()!=0){
            propertyUserHouseMapper.saveList(userHouseList);
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
        HouseMemberEntity memberEntity = propertyRelationMapper.selectById(id);
        if (memberEntity.getRelation()==1){
            proprietorMapper.delete(new QueryWrapper<ProprietorEntity>().eq("real_name",memberEntity.getName()).eq("house_id",memberEntity.getHouseId()).eq("community_id",memberEntity.getCommunityId()).eq("mobile",memberEntity.getMobile()));
        }
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
