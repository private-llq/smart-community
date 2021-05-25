package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IRelationService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.RelationCarEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.qo.proprietor.RelationCarsQO;
import com.jsy.community.qo.proprietor.RelationQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.RelationCarsVO;
import com.jsy.community.vo.RelationVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 家属操作接口
 * @author: Hu
 * @since: 2020/12/10 16:37
 * @Param:
 * @return:
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class RelationServiceImpl implements IRelationService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RelationCarMapper relationCarMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseMemberMapper houseMemberMapper;

    @Autowired
    private RelationMapper relationMapper;

    @Autowired
    private UserHouseMapper userHouseMapper;



    /**
     * @Description: 删除车辆
     * @author: Hu
     * @since: 2021/5/21 13:56
     * @Param: [uid, id]
     * @return: void
     */
    @Override
    public void delCar(String uid, Long id) {
        relationCarMapper.delete(new QueryWrapper<RelationCarEntity>().eq("uid",uid).eq("id",id));
        rabbitTemplate.convertAndSend("exchange_car_topics","queue.car.delete",id);
    }

    /**
     * @Description: 封装新增es车辆实体类方法
     * @author: Hu
     * @since: 2021/3/26 11:22
     * @Param: car,relationQo,houseEntity
     * @return: ElasticsearchCarQO
     */
    public ElasticsearchCarQO getInsetElasticsearchCarQO(RelationCarsQO car,RelationQO relationQo,HouseEntity houseEntity){
        ElasticsearchCarQO elasticsearchCarQO = new ElasticsearchCarQO();
        elasticsearchCarQO.setId(car.getId());
        elasticsearchCarQO.setCommunityId(relationQo.getCommunityId());
        elasticsearchCarQO.setCarPlate(car.getCarPlate());
        elasticsearchCarQO.setCarType(car.getCarType());
        elasticsearchCarQO.setCarTypeText(BusinessEnum.CarTypeEnum.getCode(car.getCarType()));
        elasticsearchCarQO.setOwner(relationQo.getName());
        elasticsearchCarQO.setIdCard(relationQo.getIdCard());
        elasticsearchCarQO.setMobile(relationQo.getMobile());
        elasticsearchCarQO.setOwnerType(2);
        elasticsearchCarQO.setOwnerTypeText("家属");
        elasticsearchCarQO.setRelationshipId(relationQo.getId()+"");
        elasticsearchCarQO.setHouseId(relationQo.getHouseId());
        elasticsearchCarQO.setBuilding(houseEntity.getBuilding());
        elasticsearchCarQO.setFloor(houseEntity.getFloor());
        elasticsearchCarQO.setUnit(houseEntity.getUnit());
        elasticsearchCarQO.setNumber(houseEntity.getNumber());
        elasticsearchCarQO.setHouseType(houseEntity.getHouseType());
        elasticsearchCarQO.setHouseTypeText(houseEntity.getHouseType()==1?"商铺":"住宅");
        elasticsearchCarQO.setCreateTime(LocalDateTime.now());
        System.out.println(elasticsearchCarQO);
        return elasticsearchCarQO;
    }
    /**
     * @Description: 封装修改es车辆实体类方法
     * @author: Hu
     * @since: 2021/3/26 11:22
     * @Param: car
     * @return: ElasticsearchCarQO
     */
    public ElasticsearchCarQO getUpdateElasticsearchCarQO(RelationCarsQO car){
        ElasticsearchCarQO elasticsearchCarQO = new ElasticsearchCarQO();
        elasticsearchCarQO.setId(car.getId());
        elasticsearchCarQO.setCarPlate(car.getCarPlate());
        elasticsearchCarQO.setCarType(car.getCarType());
        elasticsearchCarQO.setCarTypeText(BusinessEnum.CarTypeEnum.getCode(car.getCarType()));
        return elasticsearchCarQO;
    }



    /**
     * @Description: 添加家属
     * @author: Hu
     * @since: 2021/5/21 13:55
     * @Param: [relationQo]
     * @return: void
     */
    @Override
    @Transactional
    public void addRelation(RelationQO relationQo) {
        HouseMemberEntity houseMemberEntity = new HouseMemberEntity();
        houseMemberEntity.setId(SnowFlake.nextId());
        if (relationQo.getIdentificationType()==0&&relationQo.getIdentificationType()==null){
            relationQo.setIdentificationType(1);
        }
        houseMemberEntity.setIdentificationType(relationQo.getIdentificationType());
        houseMemberEntity.setHouseId(relationQo.getHouseId());
        houseMemberEntity.setHouseholderId(relationQo.getUserId());
        houseMemberEntity.setName(relationQo.getName());
        houseMemberEntity.setCommunityId(relationQo.getCommunityId());
        houseMemberEntity.setIdCard(relationQo.getIdCard());
        houseMemberEntity.setMobile(relationQo.getMobile());
        houseMemberEntity.setRelation(relationQo.getRelation());
        houseMemberEntity.setSex(relationQo.getSex());
        houseMemberMapper.insert(houseMemberEntity);
        relationQo.setId(houseMemberEntity.getId());
        //添加车辆信息

        HouseEntity houseEntity = houseMapper.selectById(relationQo.getHouseId());

        List<RelationCarsQO> cars = relationQo.getCars();
            if (cars.size()>0) {
                for (RelationCarsQO car : cars) {
                    car.setId(SnowFlake.nextId());
                    car.setUid(relationQo.getUserId());
                    car.setRelationType(1);
                    car.setCommunityId(relationQo.getCommunityId());
                    car.setOwner(relationQo.getName());
                    car.setMobile(relationQo.getMobile());
                    car.setRelationshipId(houseMemberEntity.getId());
                    car.setIdCard(houseMemberEntity.getIdCard());
                    car.setDrivingLicenseUrl(car.getDrivingLicenseUrl());
                }
                for (RelationCarsQO car : cars) {
                    rabbitTemplate.convertAndSend("exchange_car_topics","queue.car.insert",getInsetElasticsearchCarQO(car,relationQo,houseEntity));
                }
                relationMapper.addCars(cars);
            }

    }



    /**
     * @Description: 查询业主下面的家属
     * @author: Hu
     * @since: 2021/5/21 13:55
     * @Param: [id, houseId]
     * @return: java.util.List<com.jsy.community.vo.RelationVO>
     */
    @Override
    public List<RelationVO> selectID(String id,Long houseId) {
        List<HouseMemberEntity> houseMemberEntities = relationMapper.selectID(id,houseId);
        List<RelationVO> list = new ArrayList<>();
        for (HouseMemberEntity houseMemberEntity : houseMemberEntities) {
            RelationVO relationVO = new RelationVO();
            BeanUtils.copyProperties(houseMemberEntity,relationVO);
            relationVO.setRelationText(BusinessEnum.RelationshipEnum.getCode(houseMemberEntity.getRelation()));
            list.add(relationVO);
        }
        return list;
    }

    /**
     * @Description: 查询业主下面的家属详情
     * @author: Hu
     * @since: 2021/5/21 13:55
     * @Param: [RelationId, userId]
     * @return: com.jsy.community.vo.RelationVO
     */
    @Override
    public RelationVO selectOne(Long RelationId, String userId) {
        HouseMemberEntity houseMemberEntity = houseMemberMapper.selectById(RelationId);
        if (houseMemberEntity==null){
            throw new ProprietorException("数据不存在！");
        }
        List<RelationCarEntity> carEntities = relationCarMapper.selectList(new QueryWrapper<RelationCarEntity>().eq("relationship_id", RelationId));
        RelationVO relationVO = new RelationVO();
        relationVO.setIdentificationType(houseMemberEntity.getIdentificationType());
        relationVO.setId(houseMemberEntity.getId());
        relationVO.setMobile(houseMemberEntity.getMobile());
        relationVO.setName(houseMemberEntity.getName());
        relationVO.setIdCard(houseMemberEntity.getIdCard());
        relationVO.setRelation(houseMemberEntity.getRelation());
        relationVO.setRelationText(BusinessEnum.RelationshipEnum.getCode(houseMemberEntity.getRelation()));
        relationVO.setSex(houseMemberEntity.getSex());
        relationVO.setRelationText( BusinessEnum.RelationshipEnum.getCode(houseMemberEntity.getRelation()) );
        relationVO.setCommunityId(houseMemberEntity.getCommunityId());
        relationVO.setIdentificationTypeText(BusinessEnum.IdentificationType.getKv().get(houseMemberEntity.getIdentificationType()));
        relationVO.setHouseId(houseMemberEntity.getHouseId());
        List<RelationCarsVO> objects = new ArrayList<>();
        //封装车辆信息
        if (objects!=null){
            for (RelationCarEntity carEntity : carEntities) {
                RelationCarsVO relationCarsVO = new RelationCarsVO();
                relationCarsVO.setId(carEntity.getId());
                relationCarsVO.setCarType(carEntity.getCarType());
                relationCarsVO.setDrivingLicenseUrl(carEntity.getDrivingLicenseUrl());
                relationCarsVO.setCarPlate(carEntity.getCarPlate());
                relationCarsVO.setCarTypeText(BusinessEnum.CarTypeEnum.getCode(carEntity.getCarType()));
                objects.add(relationCarsVO);
            }
        }
        relationVO.setCars(objects);
        return relationVO;
    }


    /**
     * @Description: 修改家属信息
     * @author: Hu
     * @since: 2021/5/21 13:54
     * @Param: [houseMemberEntity]
     * @return: void
     */
    @Override
    public void updateByRelationId(HouseMemberEntity houseMemberEntity) {
        houseMemberMapper.updateById(houseMemberEntity);
    }


    /**
     * @Description: 查询一条表单回填
     * @author: Hu
     * @since: 2021/5/21 13:54
     * @Param: [relationId]
     * @return: com.jsy.community.entity.HouseMemberEntity
     */
    @Override
    public HouseMemberEntity updateFormBackFillId(Long relationId) {
        return houseMemberMapper.selectById(relationId);
    }



    /**
     * @Description: 修改用户家属和汽车
     * @author: Hu
     * @since: 2021/5/21 13:54
     * @Param: [relationQo]
     * @return: void
     */
    @Override
    @Transactional
    public void updateUserRelationDetails(RelationQO relationQo) {
        relationMapper.updateUserRelationDetails(relationQo);
        List<RelationCarsQO> cars = relationQo.getCars();
        HouseEntity houseEntity = houseMapper.selectById(relationQo.getHouseId());
        if(cars.size()>0){
            for (RelationCarsQO car : cars) {
                if (car.getId()==null||car.getId()==0){
                    car.setId(SnowFlake.nextId());
                    car.setUid(relationQo.getUserId());
                    car.setRelationType(1);
                    car.setCommunityId(relationQo.getCommunityId());
                    car.setOwner(relationQo.getName());
                    car.setMobile(relationQo.getMobile());
                    car.setRelationshipId(relationQo.getId());
                    car.setIdCard(relationQo.getIdCard());
                    car.setDrivingLicenseUrl(car.getDrivingLicenseUrl());
                    relationMapper.insertOne(car);
                    rabbitTemplate.convertAndSend("exchange_car_topics","queue.car.insert",getInsetElasticsearchCarQO(car,relationQo,houseEntity));
                }else {
                    relationMapper.updateUserRelationCar(car);
                    rabbitTemplate.convertAndSend("exchange_car_topics","queue.car.update",getUpdateElasticsearchCarQO(car));
                }
            }
        }
    }



    /**
     * @Description: 房间验证
     * @author: Hu
     * @since: 2021/5/21 13:54
     * @Param: [relationQo]
     * @return: com.jsy.community.entity.UserHouseEntity
     */
    @Override
    public UserHouseEntity getHouse(RelationQO relationQo) {
        UserHouseEntity entity = userHouseMapper.selectOne(new QueryWrapper<UserHouseEntity>().eq("uid", relationQo.getUserId()).eq("house_id", relationQo.getHouseId()).eq("community_id", relationQo.getCommunityId()));
        return entity;
    }



    /**
     * @Description: 删除家属信息和相关车辆信息
     * @author: Hu
     * @since: 2021/5/21 13:53
     * @Param: [id, uid]
     * @return: void
     */
    @Override
    @Transactional
    public void deleteHouseMemberCars(Long id,String uid) {
        houseMemberMapper.delete(new QueryWrapper<HouseMemberEntity>().eq("householder_id",uid).eq("id",id));
        relationCarMapper.delete(new QueryWrapper<RelationCarEntity>().eq("relationship_id",id).eq("uid",uid));
        List<RelationCarEntity> relationCarEntities = relationCarMapper.selectList(new QueryWrapper<RelationCarEntity>().eq("relationship_id", id).eq("uid", uid));
        if (relationCarEntities.size()!=0){
            for (RelationCarEntity relationCarEntity : relationCarEntities) {
                rabbitTemplate.convertAndSend("exchange_car_topics","queue.car.delete",relationCarEntity.getId());
            }
        }


    }

    /**
     * @Description: 判断是否是指定小区家属
     * @Param: [mobile, communityId]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/23
     **/
    @Override
    public boolean isHouseMember(String mobile, Long communityId){
        Integer count = houseMemberMapper.selectCount(new QueryWrapper<HouseMemberEntity>().eq("mobile", mobile).eq("community_id", communityId));
        return count > 0;
    }
    
}
