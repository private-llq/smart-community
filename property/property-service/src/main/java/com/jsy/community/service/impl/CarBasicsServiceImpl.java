package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarBasicsService;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBasicsEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.CarBasicsMapper;
import com.jsy.community.mapper.CarPositionMapper;
import com.jsy.community.qo.property.CarBasicsMonthQO;
import com.jsy.community.qo.property.CarBasicsRuleQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;


import javax.annotation.Resource;
import java.util.List;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarBasicsServiceImpl extends ServiceImpl<CarBasicsMapper,CarBasicsEntity> implements ICarBasicsService {
    @Resource
    private CarBasicsMapper carBasicsMapper;
    @Resource
    private ICarPositionService positionService;
    @Resource
    private CarPositionMapper carPositionMapper;

    @Override
    public boolean addBasics(CarBasicsRuleQO carBasicsRuleQO, String uid, Long communityId) {
        CarBasicsEntity carBasicsEntity = carBasicsMapper.selectOne(new QueryWrapper<CarBasicsEntity>().eq("community_id", communityId));
        //已使用车位（月租+业主）
        Integer integer = positionService.selectCarPositionUseAmount(communityId);
        // 查询小区下所有车位
        Integer allCarPosition = carPositionMapper.getAllCarPositionByCommunity(communityId).size();

        if(carBasicsRuleQO.getDwellTime()<0){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "停留时间输入数据有误");
        }
        if (carBasicsRuleQO.getMaxNumber()<0){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "最大入场数输入数据有误");
        }
        if (carBasicsRuleQO.getMaxNumber()>(allCarPosition-integer)){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "临时车入场数必须小于剩余车位数");
        }
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
        if(carBasicsMonthQO.getMonthlyPayment()<0&&carBasicsMonthQO.getMonthMaxTime()<0){
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "输入数据有误");
        }
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
