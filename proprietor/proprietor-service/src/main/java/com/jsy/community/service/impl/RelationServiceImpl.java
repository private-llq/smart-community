package com.jsy.community.service.impl;

import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.RelationMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.RelationCarsQo;
import com.jsy.community.qo.RelationQo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户家属信息
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


    /**
     * 添加家属
     * @param relationQo
     * @return
     */
    @Override
    @Transactional
    public Boolean addRelation(RelationQo relationQo) {


        List<RelationCarsQo> cars = relationQo.getCars();
            if (cars.size()>0) {
                for (RelationCarsQo car : cars) {
                    car.setUid(relationQo.getUserId());
                    car.setCommunityId(relationQo.getCommunityId());
                    car.setOwner(relationQo.getName());
                    car.setPhoneTel(relationQo.getPhoneTel());
                }
                relationMapper.addCars(cars);
            }


            HouseMemberEntity houseMemberEntity = new HouseMemberEntity();
            houseMemberEntity.setHouseId(relationQo.getHouseId());
            houseMemberEntity.setHouseholderId(relationQo.getUserId());
            houseMemberEntity.setName(relationQo.getName());
            houseMemberEntity.setCommunityId(relationQo.getCommunityId());
            houseMemberEntity.setIdCard(relationQo.getIdNumber());
            houseMemberEntity.setMobile(relationQo.getPhoneTel());
            houseMemberEntity.setRelation(relationQo.getConcern());
            houseMemberEntity.setSex(relationQo.getSex());
            houseMemberMapper.insert(houseMemberEntity);
            return true;



    }

    @Override
    public void saveCars(List<RelationCarsQo> cars) {
        relationMapper.addCars(cars);
    }
}
