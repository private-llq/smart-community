package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarBasicsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBasicsEntity;
import com.jsy.community.mapper.CarBasicsMapper;
import com.jsy.community.qo.property.CarBasicsMonthQO;
import com.jsy.community.qo.property.CarBasicsRuleQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;


import javax.annotation.Resource;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarBasicsServiceImpl extends ServiceImpl<CarBasicsMapper,CarBasicsEntity> implements ICarBasicsService {
    @Resource
    private CarBasicsMapper carBasicsMapper;

    @Override
    public boolean addBasics(CarBasicsRuleQO carBasicsRuleQO, String uid, Long communityId) {
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id", communityId));
        if(carBasicsEntity!=null){
           BeanUtils.copyProperties(carBasicsRuleQO,carBasicsEntity);
          return carBasicsMapper.updateById(carBasicsEntity) == 1;
       }else {
            CarBasicsEntity carBasicsEntity1 = new CarBasicsEntity();
            carBasicsEntity1.setId(SnowFlake.nextId());
            carBasicsEntity1.setUid(uid);
            carBasicsEntity1.setCommunityId(communityId);
            BeanUtils.copyProperties(carBasicsRuleQO,carBasicsEntity1);
            return  carBasicsMapper.insert(carBasicsEntity1) == 1;
    }

    }

    @Override
    public CarBasicsEntity findOne(Long communityId) {
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id", communityId));
        return carBasicsEntity;
    }

    @Override
    public boolean addExceptionCar(Integer exceptionCar,String uid,Long communityId) {
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id",communityId));
        if (carBasicsEntity!=null){
            carBasicsEntity.setExceptionCar(exceptionCar);
            carBasicsMapper.updateById(carBasicsEntity);
        }else {
            CarBasicsEntity carBasicsEntity1 = new CarBasicsEntity();
            carBasicsEntity1.setCommunityId(communityId);
            carBasicsEntity1.setId(SnowFlake.nextId());
            carBasicsEntity1.setUid(uid);
            carBasicsEntity1.setExceptionCar(exceptionCar);
            carBasicsMapper.insert(carBasicsEntity1);
        }
        return false;
    }

    @Override
    public boolean addMonthlyPayment(CarBasicsMonthQO carBasicsMonthQO, String userId, Long adminCommunityId) {
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id", adminCommunityId));
        if (carBasicsEntity != null){
            BeanUtils.copyProperties(carBasicsMonthQO,carBasicsEntity);
           return carBasicsMapper.updateById(carBasicsEntity) == 1;
        }else {
            CarBasicsEntity carBasicsEntity1 = new CarBasicsEntity();
            carBasicsEntity1.setUid(userId);
            carBasicsEntity1.setCommunityId(adminCommunityId);
            carBasicsEntity1.setId(SnowFlake.nextId());
            BeanUtils.copyProperties(carBasicsMonthQO,carBasicsEntity1);
            return  carBasicsMapper.insert(carBasicsEntity1) ==1;
        }
    }


}
