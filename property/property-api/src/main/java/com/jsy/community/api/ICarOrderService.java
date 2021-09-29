package com.jsy.community.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarOrderEntity;

import java.time.LocalDateTime;

public interface ICarOrderService extends IService<CarOrderEntity> {

    //根据条件查询车辆最后订单的状态
    CarOrderEntity selectCarOrderStatus(Long communityId, String plateNum,Integer type);
   //删除未支付的订单
    void deletedNOpayOrder(String plateNum, Long communityId, LocalDateTime beginTime);
    //根据订单号查询订单记录
    public CarOrderEntity selectOneOrder(String orderNumber);
    //修改订单
    public  boolean updateOrder(CarOrderEntity carOrderEntity,String orderNumber);
}
