package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarLocationService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarLocationEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.CarLocationMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarLocationServiceImpl extends ServiceImpl<CarLocationMapper,CarLocationEntity> implements ICarLocationService {
    @Autowired
    private CarLocationMapper locationMapper;

    @Override
    public Page<CarLocationEntity> listLocation(BaseQO<CarLocationEntity> baseQO, Long communityId) {


        Page<CarLocationEntity> page = new Page<>(baseQO.getPage(),baseQO.getSize());

        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<CarLocationEntity>().eq("community_id", communityId);

        //baseQO.setCommunityId(communityId);
        CarLocationEntity query = baseQO.getQuery();
        if (!StringUtils.isEmpty(query.getEquipmentLocation())){
            queryWrapper.like("equipment_location",baseQO.getQuery().getEquipmentLocation());
        }

        Page<CarLocationEntity> carLocationEntityPage = locationMapper.selectPage(page, queryWrapper);
        System.out.println(carLocationEntityPage.getSize());
        return carLocationEntityPage;
    }

    @Override
    public boolean addLocation(String equipmentLocation, Long communityId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id",communityId);
        queryWrapper.eq("equipment_location",equipmentLocation);
        List<CarLocationEntity> carLocationEntities = locationMapper.selectList(queryWrapper);
        if (carLocationEntities.size()>0) {
            throw new PropertyException(500,"该位置已存在");
        }
        CarLocationEntity locationEntity = new CarLocationEntity();
        locationEntity.setLocationId(UUID.randomUUID().toString());
        locationEntity.setCommunityId(communityId);
        locationEntity.setEquipmentLocation(equipmentLocation);
        return  locationMapper.insert(locationEntity) == 1;


    }

    @Override
    public boolean updateLocation(String equipmentLocation, String locationId, Long adminCommunityId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id",locationId)
                    .eq("community_id",adminCommunityId);
        CarLocationEntity locationEntity = new CarLocationEntity();
        locationEntity.setEquipmentLocation(equipmentLocation);
        return locationMapper.update(locationEntity,queryWrapper)==1;

    }

    @Override
    public boolean deleteLocation(String locationId, Long adminCommunityId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("location_id",locationId)
                .eq("community_id",adminCommunityId);
        return  locationMapper.delete(queryWrapper)==1;
    }

    @Override
    public CarLocationEntity findOne(String locationId) {
        QueryWrapper<CarLocationEntity> queryWrapper = new QueryWrapper<CarLocationEntity>().eq("location_id", locationId);
        CarLocationEntity locationEntity = locationMapper.selectOne(queryWrapper);
        return locationEntity;
    }

    @Override
    public List<CarLocationEntity> selectList(Long adminCommunityId) {
        List<CarLocationEntity> locationEntityList = locationMapper.selectList(new QueryWrapper<CarLocationEntity>().eq("community_id",adminCommunityId));
        return locationEntityList;
    }
}
