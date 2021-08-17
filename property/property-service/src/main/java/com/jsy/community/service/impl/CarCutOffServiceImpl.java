package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarCutOffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.entity.property.CarProprietorEntity;
import com.jsy.community.mapper.CarCutOffMapper;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.utils.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarCutOffServiceImpl extends ServiceImpl<CarCutOffMapper,CarCutOffEntity> implements ICarCutOffService {
   @Autowired
   private CarCutOffMapper carCutOffMapper;

    @Override
    public PageInfo<CarCutOffEntity> selectPage(CarCutOffQO carCutOffQO, Long adminCommunityId) {

        QueryWrapper<CarCutOffEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(carCutOffQO.getCarNumber())){
            queryWrapper.like("car_number",carCutOffQO.getCarNumber());
        }
        if (StringUtils.isNotBlank(carCutOffQO.getCarType())){
            queryWrapper.like("car_type",carCutOffQO.getCarType());
        }
        if (StringUtils.isNotBlank(carCutOffQO.getAccess())){
            queryWrapper.like("access",carCutOffQO.getAccess());
        }
        queryWrapper.eq("community_id",adminCommunityId);

        Page<CarCutOffEntity> page = new Page<CarCutOffEntity>(carCutOffQO.getPage(),carCutOffQO.getSize());
        /*if (carCutOffQO.getPage() == 0 || carCutOffQO.getPage() == null){
            carCutOffQO.setPage(10L);
        }*/

        Page<CarCutOffEntity> selectPage = carCutOffMapper.selectPage(page, queryWrapper);

        PageInfo<CarCutOffEntity> pageInfo = new PageInfo<>();
        page.setTotal(selectPage.getTotal());
        page.setRecords(page.getRecords());
        page.setCurrent(page.getCurrent());
        page.setSize(page.getSize());

        return pageInfo;
    }
}
