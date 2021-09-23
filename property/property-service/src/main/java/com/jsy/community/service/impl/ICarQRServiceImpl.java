package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICaQRService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarQREntity;
import com.jsy.community.mapper.CarQRMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = Const.version, group = Const.group_property)
public class ICarQRServiceImpl extends ServiceImpl<CarQRMapper, CarQREntity> implements ICaQRService {
    @Autowired
    private  CarQRMapper carQRMapper;

    @Override
    public Boolean addQRCode(String path, Long communityId) {
        CarQREntity carQREntity = new CarQREntity();
        carQREntity.setId(SnowFlake.nextId());
        carQREntity.setCommunityId(communityId);
        carQREntity.setPath(path);
        return carQRMapper.insert(carQREntity)==1;
    }

    @Override
    public CarQREntity findOne(Long communityId) {
        CarQREntity carQREntity = carQRMapper.selectOne(new QueryWrapper<CarQREntity>().eq("community_id", communityId));
        return carQREntity;
    }

    @Override
    public boolean updateQRCode(CarQREntity carQREntity) {
        return carQRMapper.updateById(carQREntity)==1;
    }
}
