package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarCutOffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.mapper.CarCutOffMapper;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarCutOffServiceImpl extends ServiceImpl<CarCutOffMapper,CarCutOffEntity> implements ICarCutOffService {
   @Autowired
   private CarCutOffMapper carCutOffMapper;

    @Override
    public PageInfo<CarCutOffEntity> selectPage(CarCutOffQO carCutOffQO, Long adminCommunityId) {

        QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(carCutOffQO.getCarNumber())){
            queryWrapper.like("car_number",carCutOffQO.getCarNumber());
        }

        if (!StringUtils.isEmpty(carCutOffQO.getCarType())){
            queryWrapper.like("car_type",carCutOffQO.getCarType());
        }

        if (!StringUtils.isEmpty(carCutOffQO.getAccess())){
            queryWrapper.like("access",carCutOffQO.getAccess());
        }

        queryWrapper.eq("community_id",adminCommunityId);

        Page<CarCutOffEntity> page = new Page<CarCutOffEntity>(carCutOffQO.getPage(),carCutOffQO.getSize());
        PageInfo<CarCutOffEntity> pageInfo = new PageInfo<>();
        if (carCutOffQO.getPage() == 0 || carCutOffQO.getPage() == null){
            carCutOffQO.setPage(10L);
        }
        Page<CarCutOffEntity> selectPage = carCutOffMapper.selectPage(page, queryWrapper);
        pageInfo.setRecords(selectPage.getRecords());
        pageInfo.setTotal(selectPage.getTotal());
        pageInfo.setCurrent(selectPage.getCurrent());
        pageInfo.setSize(selectPage.getSize());

        return pageInfo;
    }

    @Override
    public boolean addCutOff(CarCutOffEntity carCutOffEntity) {
        carCutOffEntity.setId(SnowFlake.nextId());
        return carCutOffMapper.insert(carCutOffEntity) == 1;
    }

    @Override
    public List<CarCutOffEntity> selectAccess(String carNumber, Integer state) {
        QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("car_number",carNumber).eq("state",state);
        List<CarCutOffEntity> carCutOffEntityList = carCutOffMapper.selectList(queryWrapper);
        return carCutOffEntityList;
    }

    @Override
    public boolean updateCutOff(CarCutOffEntity carCutOffEntity) {
        return  carCutOffMapper.updateById(carCutOffEntity) == 1;
    }
}
