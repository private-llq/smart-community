package com.jsy.community.service.impl;

import com.jsy.community.api.IRelationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.RelationMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.vo.RelationCarsVO;
import com.jsy.community.vo.RelationVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户家属信息
 */
@DubboService(version = Const.version, group = Const.group)
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
     * @param relationVO
     * @return
     */
    @Override
    @Transactional
    public Boolean addRelation(RelationVO relationVO) {

            UserEntity userEntity = new UserEntity();
            userEntity.setRealName(relationVO.getName());
            userEntity.setMobile(relationVO.getPhoneTel());
            userEntity.setSex(relationVO.getSex());

            //当前登录用户id暂时暂时写死
            userEntity.setHouseholderId(12l);
            userEntity.setIdCard(relationVO.getIdNumber());
            userMapper.insert(userEntity);


            List<RelationCarsVO> cars = relationVO.getCars();
            if (cars.size()>0) {
                for (RelationCarsVO car : cars) {
                    car.setUid(userEntity.getId());
                    car.setCommunityId(relationVO.getCommunityId());
                    car.setOwner(relationVO.getName());
                    car.setPhoneTel(relationVO.getPhoneTel());
                }
                relationMapper.addCars(cars);
            }


            HouseMemberEntity houseMemberEntity = new HouseMemberEntity();
            houseMemberEntity.setUid(userEntity.getId());
            houseMemberEntity.setCommunityId(relationVO.getCommunityId());

            //当前登录用户id暂时暂时写死
            houseMemberEntity.setHouseholderId(12l);
            houseMemberEntity.setHouseId(relationVO.getHouseId());
            houseMemberMapper.insert(houseMemberEntity);
            return true;



    }
}
