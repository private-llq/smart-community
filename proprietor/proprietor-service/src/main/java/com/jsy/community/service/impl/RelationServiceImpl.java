package com.jsy.community.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.RelationMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.RelationCarsQo;
import com.jsy.community.qo.RelationQo;
import com.jsy.community.utils.RealnameAuthUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.RelationCarsVO;
import com.jsy.community.vo.RelationVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private CarMapper carMapper;

    @Autowired
    private HouseMemberMapper houseMemberMapper;

    @Autowired
    private RelationMapper relationMapper;

    @Autowired
    private RealnameAuthUtils realnameAuthUtils;


    /**
     * 添加家属
     * @param relationQo
     * @return
     */
    @Override
    @Transactional
    public Boolean addRelation(RelationQo relationQo) {
        //实名认证
        if(BusinessConst.IDENTIFICATION_TYPE_IDCARD.equals(relationQo.getIdentificationType())){
            if(!realnameAuthUtils.twoElements(relationQo.getName(), relationQo.getIdNumber())){
                return false;
            }
        }

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
        houseMemberEntity.setIdCard(relationQo.getIdNumber());
        houseMemberEntity.setMobile(relationQo.getPhoneTel());
        houseMemberEntity.setRelation(relationQo.getConcern());
        houseMemberEntity.setSex(relationQo.getSex());
        houseMemberEntity.setPersonType(relationQo.getPersonType());


        houseMemberMapper.insert(houseMemberEntity);

        List<RelationCarsQo> cars = relationQo.getCars();
            if (cars.size()>0) {
                for (RelationCarsQo car : cars) {
                    car.setId(SnowFlake.nextId());
                    car.setUid(relationQo.getUserId());
                    car.setCommunityId(relationQo.getCommunityId());
                    car.setOwner(relationQo.getName());
                    car.setPhoneTel(relationQo.getPhoneTel());
                    car.setHouseMemberId(houseMemberEntity.getId());
                    car.setDrivingLicenseUrl(car.getDrivingLicenseUrl());
                }
                relationMapper.addCars(cars);
            }

            return true;



    }

    /**
     * 查询业主下面的家属
     * @param id
     * @return
     */
    @Override
    public List<HouseMemberEntity> selectID(String id,Long houseId) {
        List<HouseMemberEntity> houseMemberEntities = relationMapper.selectID(id,houseId);
        return houseMemberEntities;
    }
    /**
     * 查询业主下面的家属详情
     * @param RelationId
     * @return
     */
    @Override
    public RelationVO selectOne(Long RelationId, String userId) {
        HouseMemberEntity houseMemberEntity = houseMemberMapper.selectById(RelationId);
        List<CarEntity> carEntities = carMapper.selectList(new QueryWrapper<CarEntity>().eq("house_member_id", RelationId));
        RelationVO relationVO = new RelationVO();
        relationVO.setPhoneTel(houseMemberEntity.getMobile());
        relationVO.setName(houseMemberEntity.getName());
        relationVO.setIdNumber(houseMemberEntity.getIdCard());
        relationVO.setConcern(houseMemberEntity.getRelation());
        relationVO.setSex(houseMemberEntity.getSex());
        relationVO.setCommunityId(houseMemberEntity.getCommunityId());
        relationVO.setHouseId(houseMemberEntity.getHouseId());
        relationVO.setUserId(houseMemberEntity.getHouseholderId());
        List<RelationCarsVO> objects = new ArrayList<>();
        for (CarEntity carEntity : carEntities) {
            RelationCarsVO relationCarsVO = new RelationCarsVO();
            relationCarsVO.setUid(carEntity.getUid());
            relationCarsVO.setCarPosition(carEntity.getCarPositionId());
            relationCarsVO.setCheckStatus(carEntity.getCheckStatus());
            relationCarsVO.setOwner(carEntity.getOwner());
            relationCarsVO.setId(carEntity.getId());
            relationCarsVO.setPhoneTel(carEntity.getContact());
            relationCarsVO.setCarType(carEntity.getCarType());
            relationCarsVO.setCarImgURL(carEntity.getCarImageUrl());
            relationCarsVO.setCarId(carEntity.getCarPlate());
            relationCarsVO.setCommunityId(carEntity.getCommunityId());
            relationCarsVO.setCarTypeName(carEntity.getCarType()==1?"微型车":carEntity.getCarType()==2?"小型车":carEntity.getCarType()==3?"紧凑型车":carEntity.getCarType()==4?"中型车":"中大型车");
            objects.add(relationCarsVO);
        }
        relationVO.setCars(objects);

        return relationVO;
    }
    /**
     * 修改家属信息
     * @param houseMemberEntity
     * @return
     */
    @Override
    public void updateByRelationId(HouseMemberEntity houseMemberEntity) {
        houseMemberMapper.updateById(houseMemberEntity);
    }
    /**
     * 查询一条表单回填
     * @param relationId
     * @return
     */
    @Override
    public HouseMemberEntity updateFormBackFillId(Long relationId) {
        return houseMemberMapper.selectById(relationId);
    }

    /**
     * @Description: 修改用户家属和汽车
     * @author: Hu
     * @since: 2020/12/21 9:58
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void updateUserRelationDetails(RelationQo relationQo) {
        relationMapper.updateUserRelationDetails(relationQo);
        List<RelationCarsQo> cars = relationQo.getCars();
        for (RelationCarsQo relationCarsQo : cars) {
            relationMapper.updateUserRelationCar(relationCarsQo);
        }
    }
    /**
     * @Description: 删除家属信息
     * @author: Hu
     * @since: 2020/12/25 14:46
     * @Param:
     * @return:
     */
    @Override
    @Transactional
    public void deleteHouseMemberCars(Long id) {
        houseMemberMapper.deleteById(id);
        carMapper.delete(new QueryWrapper<CarEntity>().eq("house_member_id",id));
    }

    /**
     * @Description: 判断是否是指定小区家属
     * @Param: [mobile, communityId]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/12/23
     **/
    public boolean isHouseMember(String mobile,Long communityId){
        Integer count = houseMemberMapper.selectCount(new QueryWrapper<HouseMemberEntity>().eq("mobile", mobile).eq("community_id", communityId));
        return count > 0;
    }
    
}
