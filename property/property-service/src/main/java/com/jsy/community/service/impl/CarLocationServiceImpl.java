package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarLocationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarLocationEntity;
import com.jsy.community.mapper.CarLocationMapper;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarLocationServiceImpl extends ServiceImpl<CarLocationMapper,CarLocationEntity> implements ICarLocationService {
    @Autowired
    private CarLocationMapper locationMapper;

    @Override
    public List<CarLocationEntity> listLocation(Long communityId) {

        List<CarLocationEntity> locationEntityList = locationMapper.selectList(new QueryWrapper<CarLocationEntity>().eq("community_id",communityId));
        return locationEntityList;
    }

    @Override
    public boolean addLocation(String equipmentLocation, Long communityId) {
        CarLocationEntity locationEntity = new CarLocationEntity();
        locationEntity.setLocationId(SnowFlake.nextId());
        locationEntity.setCommunityId(communityId);
        locationEntity.setEquipmentLocation(equipmentLocation);
        return  locationMapper.insert(locationEntity) == 1;


    }

    @Override
    public boolean updateLocation(String equipmentLocation, Long locationId, Long adminCommunityId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id",locationId)
                    .eq("community_id",adminCommunityId);
        CarLocationEntity locationEntity = new CarLocationEntity();
        locationEntity.setEquipmentLocation(equipmentLocation);
        return locationMapper.update(locationEntity,queryWrapper)==1;

    }

    @Override
    public boolean deleteLocation(Long locationId, Long adminCommunityId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id",locationId)
                .eq("community_id",adminCommunityId);
        return  locationMapper.delete(queryWrapper)==1;
    }

    @Override
    public CarLocationEntity findOne(Long locationId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<CarLocationEntity>().eq("location_id", locationId);
        CarLocationEntity locationEntity = locationMapper.selectOne(queryWrapper);
        return locationEntity;
    }
}
