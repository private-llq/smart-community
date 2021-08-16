package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.property.CarCutOffQO;
import com.jsy.community.utils.PageInfo;

public interface ICarCutOffService extends IService<CarCutOffEntity> {
    /**
     * @Description: 分页查询开闸记录
     * @Param: [carCutOffQO, adminCommunityId]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.property.CarCutOffEntity>
     * @Author: Tian
     * @Date: 2021/8/16-11:58
     **/
    PageInfo<CarCutOffEntity> selectPage(CarCutOffQO carCutOffQO, Long adminCommunityId);
}
