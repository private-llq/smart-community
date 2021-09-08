package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFeeRuleRelevanceEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.qo.property.FeeRuleRelevanceQO;
import com.jsy.community.qo.property.UpdateRelevanceQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.FeeRuleVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    private AdminUserMapper adminUserMapper;
    @Autowired
    private PropertyFeeRuleRelevanceMapper propertyFeeRuleRelevanceMapper;
    @Autowired
    private CarPositionMapper carPositionMapper;
    @Autowired
    private HouseMapper houseMapper;



    /**
     * @Description: 查询当前小区的月租或属于业主的车位
     * @author: Hu
     * @since: 2021/9/7 11:11
     * @Param: [adminCommunityId]
     * @return: java.util.List<com.jsy.community.vo.HouseTypeVo>
     */
    @Override
    public List<FeeRelevanceTypeVo> getCarPosition(Long adminCommunityId, Integer type) {
        return carPositionMapper.getCarPosition(adminCommunityId,type);
    }


    /**
     * @Description: 查询当前小区业主认证过的房屋
     * @author: Hu
     * @since: 2021/9/7 11:11
     * @Param: [communityId]
     * @return: java.util.List<com.jsy.community.vo.HouseTypeVo>
     */
    @Override
    public List<FeeRelevanceTypeVo> getHouse(Long communityId) {
        List<FeeRelevanceTypeVo> list = houseMapper.getUserHouse(communityId);
        return list;
    }


    /**
     * @Description: 查询收费项目关联对象
     * @author: Hu
     * @since: 2021/9/7 11:22
     * @Param: [feeRuleRelevanceQO]
     * @return: java.util.List
     */
    @Override
    public List selectRelevance(FeeRuleRelevanceQO feeRuleRelevanceQO) {
        if (feeRuleRelevanceQO.getType()==1){
            //查房屋
            return propertyFeeRuleRelevanceMapper.selectHouse(feeRuleRelevanceQO);
        }else{
            //查车位
            return propertyFeeRuleRelevanceMapper.selectCarPosition(feeRuleRelevanceQO);
        }
    }



    /**
     * @Description: 批量新增收费项目关联目标
     * @author: Hu
     * @since: 2021/9/6 14:07
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void addRelevance(UpdateRelevanceQO updateRelevanceQO) {
        List<PropertyFeeRuleRelevanceEntity> list = new LinkedList();
        PropertyFeeRuleRelevanceEntity entity = null;
        propertyFeeRuleRelevanceMapper.delete(new QueryWrapper<PropertyFeeRuleRelevanceEntity>().eq("rule_id",updateRelevanceQO.getId()));
        List<String> idList = updateRelevanceQO.getRelevanceIdList();
        for (String id : idList) {
            entity = new PropertyFeeRuleRelevanceEntity();
            entity.setId(SnowFlake.nextId());
            entity.setRelevanceId(Long.parseLong(id));
            entity.setRuleId(updateRelevanceQO.getId());
            entity.setType(updateRelevanceQO.getType());
            list.add(entity);
        }
        if (list.size()!=0){
            propertyFeeRuleRelevanceMapper.save(list);
        }
    }

    /**
     * @Description: 删除收费项目中关联的房屋或者车位
     * @author: Hu
     * @since: 2021/9/6 13:57
     * @Param: [id]
     * @return: void
     */
    @Override
    public void deleteRelevance(Long id) {
        propertyFeeRuleRelevanceMapper.deleteById(id);
    }

    @Override
    public void statementStatus(AdminInfoVo userInfo, Integer status, Long id) {
        PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectById(id);
        if (entity!=null){
            if (status==1){
                entity.setReportStatus(1);;
            }else {
                entity.setReportStatus(0);;
            }
            propertyFeeRuleMapper.updateById(entity);
        }

    }

    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/9/1 9:50
     * @Param: [id]
     * @return: void
     */
    @Override
    @Transactional
    public void delete(Long id) {
        propertyFeeRuleRelevanceMapper.delete(new QueryWrapper<PropertyFeeRuleRelevanceEntity>().eq("rule_id",id));
        propertyFeeRuleMapper.deleteById(id);
    }

    /**
     * @Description: 新增缴费规则
     * @author: Hu
     * @since: 2021/7/20 14:26
     * @Param: [communityId, propertyFeeRuleEntity]
     * @return: void
     */
    @Override
    @Transactional
    public void saveOne(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
//        propertyFeeRuleEntity.setCommunityId(userInfo.getCommunityId());
        List<PropertyFeeRuleRelevanceEntity> list = new LinkedList<>();
        PropertyFeeRuleRelevanceEntity entity = null;
        if (propertyFeeRuleEntity.getType()==11||propertyFeeRuleEntity.getType()==12){
            propertyFeeRuleEntity.setRelevanceType(2);
        } else {
            propertyFeeRuleEntity.setRelevanceType(1);
        }
        propertyFeeRuleEntity.setCreateBy(userInfo.getUid());
        propertyFeeRuleEntity.setId(SnowFlake.nextId());
        propertyFeeRuleEntity.setStatus(0);
        propertyFeeRuleEntity.setCreateTime(LocalDateTime.now());
        Integer size = propertyFeeRuleMapper.selectCount(new QueryWrapper<PropertyFeeRuleEntity>().eq("community_id", userInfo.getCommunityId()));
        size++;
        String value = String.valueOf(size);
        //封装编号
        if (value.length()==1){
            propertyFeeRuleEntity.setSerialNumber("000"+value);
        }else {
            if(value.length()==2){
                propertyFeeRuleEntity.setSerialNumber("00"+value);
            }else {
                if (value.length()==3){
                    propertyFeeRuleEntity.setSerialNumber("0"+value);
                }else {
                    propertyFeeRuleEntity.setSerialNumber(value);
                }
            }
        }

        //封装关联类型中间表数据
        if (propertyFeeRuleEntity.getRelevance().equals(0)){
            //如果relevanceType等于2表示关联对象是车位
            if (propertyFeeRuleEntity.getType()==11||propertyFeeRuleEntity.getType()==12){
                //查询当前小区所有业主自用车位
                List<CarPositionEntity> entities = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>()
                        .eq("community_id", propertyFeeRuleEntity.getCommunityId())
                        .eq("car_pos_status", 1));
                for (CarPositionEntity carPositionEntity : entities) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(2);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(carPositionEntity.getId());
                    list.add(entity);
                }
            } else {
                //其他现在表示都关联房屋各种费用
                List<HouseEntity> house = houseMapper.selectHouseAll(propertyFeeRuleEntity.getCommunityId());
                for (HouseEntity houseEntity : house) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(1);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(houseEntity.getId());
                    list.add(entity);
                }
            }
        }else{
            //关联目标id集合
            List<String> idList = propertyFeeRuleEntity.getRelevanceIdList();
            //如果relevanceType等于2表示关联对象是车位
            if (propertyFeeRuleEntity.getType()==11||propertyFeeRuleEntity.getType()==12){
                for (String id : idList) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(2);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(Long.parseLong(id));
                    list.add(entity);
                }
            } else {
                //其他现在表示都关联房屋各种费用
                for (String id : idList) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(1);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(Long.parseLong(id));
                    list.add(entity);
                }
            }
        }
        propertyFeeRuleRelevanceMapper.save(list);
        propertyFeeRuleMapper.insert(propertyFeeRuleEntity);
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [userInfo, propertyFeeRuleEntity]
     * @return: void
     */
    @Override
    public void updateOneRule(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
        List<PropertyFeeRuleRelevanceEntity> list = new LinkedList<>();
        PropertyFeeRuleRelevanceEntity entity = null;

        if (propertyFeeRuleEntity.getType()==11||propertyFeeRuleEntity.getType()==12){
            propertyFeeRuleEntity.setRelevanceType(2);
        } else {
            propertyFeeRuleEntity.setRelevanceType(1);
        }
        propertyFeeRuleEntity.setName(BusinessEnum.FeeRuleNameEnum.getName(propertyFeeRuleEntity.getType()));
        propertyFeeRuleEntity.setRelevance(propertyFeeRuleEntity.getRelevance());


        propertyFeeRuleRelevanceMapper.delete(new QueryWrapper<PropertyFeeRuleRelevanceEntity>().eq("rule_id",propertyFeeRuleEntity.getId()));
        //封装关联类型中间表数据
        if (propertyFeeRuleEntity.getRelevance().equals(0)){
            //如果relevanceType等于2表示关联对象是车位
            if (propertyFeeRuleEntity.getType()==11||propertyFeeRuleEntity.getType()==12){
                //查询当前小区所有业主自用车位
                List<CarPositionEntity> entities = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>()
                        .eq("community_id", propertyFeeRuleEntity.getCommunityId())
                        .eq("car_pos_status", 1));
                for (CarPositionEntity carPositionEntity : entities) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(2);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(carPositionEntity.getId());
                    list.add(entity);
                }
            } else {
                //其他现在表示都关联房屋各种费用
                List<HouseEntity> house = houseMapper.getAllHouse(propertyFeeRuleEntity.getCommunityId());
                for (HouseEntity houseEntity : house) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(1);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(houseEntity.getId());
                    list.add(entity);
                }
            }
        }else{
            //关联目标id集合
            List<String> idList = propertyFeeRuleEntity.getRelevanceIdList();
            //如果relevanceType等于2表示关联对象是车位
            if (propertyFeeRuleEntity.getType()==11||propertyFeeRuleEntity.getType()==12){
                for (String id : idList) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(2);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(Long.parseLong(id));
                    list.add(entity);
                }
            } else {
                //其他现在表示都关联房屋各种费用
                for (String id : idList) {
                    entity=new PropertyFeeRuleRelevanceEntity();
                    entity.setId(SnowFlake.nextId());
                    entity.setType(1);
                    entity.setRuleId(propertyFeeRuleEntity.getId());
                    entity.setRelevanceId(Long.parseLong(id));
                    list.add(entity);
                }
            }
        }
        propertyFeeRuleRelevanceMapper.save(list);
        propertyFeeRuleMapper.updateById(propertyFeeRuleEntity);
    }


    /**
     * @Description: 启用或者停用
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [userInfo, status, id]
     * @return: void
     */
    @Override
    public void startOrOut(AdminInfoVo userInfo, Integer status,Long id) {
        PropertyFeeRuleEntity ruleEntity = propertyFeeRuleMapper.selectById(id);
        if (ruleEntity!=null){
            ruleEntity.setStatus(status);
            ruleEntity.setUpdateBy(userInfo.getUid());
            propertyFeeRuleMapper.updateById(ruleEntity);
        }

    }


    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [communityId, type]
     * @return: com.jsy.community.entity.property.PropertyFeeRuleEntity
     */
    @Override
    public PropertyFeeRuleEntity selectByOne(Long id) {
        PropertyFeeRuleEntity ruleEntity = propertyFeeRuleMapper.selectById(id);
        if (!Objects.isNull(ruleEntity)) {
            ruleEntity.setRelevanceIdList(propertyFeeRuleRelevanceMapper.selectFeeRuleList(id));
            return ruleEntity;
        }
        return null;
    }


    /**
     * @Description: 查询当前小区收费规则
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [baseQO, communityId]
     * @return: java.util.Map<java.lang.Object,java.lang.Object>
     */
    @Override
    public Map<Object, Object> findList(BaseQO<FeeRuleQO> baseQO,Long communityId) {
        FeeRuleQO query = baseQO.getQuery();
        if (baseQO.getSize()==null||baseQO.getSize()<=0){
            baseQO.setSize(10L);
        }
        QueryWrapper<PropertyFeeRuleEntity> wrapper=new QueryWrapper<PropertyFeeRuleEntity>();
        List<FeeRuleVO> page = propertyFeeRuleMapper.findList((baseQO.getPage()-1)*baseQO.getSize(),baseQO.getSize(),baseQO.getQuery());
        for (FeeRuleVO feeRuleVO : page) {
            feeRuleVO.setRelevanceIdList(propertyFeeRuleRelevanceMapper.selectFeeRuleList(feeRuleVO.getId()));
            feeRuleVO.setPeriodName(BusinessEnum.FeeRulePeriodEnum.getName(feeRuleVO.getPeriod()));
        }
        Integer total = propertyFeeRuleMapper.findTotal(baseQO.getQuery());
        Map<Object, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",page);
        return map;
    }
    
    /**
     *@Author: DKS
     *@Description: 根据收费项目名称查询收费项目id
     *@Param: feeRuleName:
     *@Date: 2021/9/7 15:27
     **/
    @Override
    public Long selectFeeRuleIdByFeeRuleName(String feeRuleName, Long communityId) {
        return propertyFeeRuleMapper.selectFeeRuleIdByFeeRuleName(feeRuleName, communityId);
    }
}
