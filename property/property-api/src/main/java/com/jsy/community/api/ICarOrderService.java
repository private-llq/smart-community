package com.jsy.community.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarOrderEntity;

public interface ICarOrderService extends IService<CarOrderEntity> {

    //根据条件查询车辆最后订单的状态
    CarOrderEntity selectCarOrderStatus(Long communityId, String plateNum,Integer type);
}
