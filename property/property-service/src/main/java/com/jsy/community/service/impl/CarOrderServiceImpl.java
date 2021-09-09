package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.mapper.CarOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;


@DubboService(version = Const.version, group = Const.group_property)
public class CarOrderServiceImpl extends ServiceImpl<CarOrderMapper, CarOrderEntity> implements ICarOrderService {

    @Resource
    private  CarOrderMapper carOrderMapper;



    //根据条件查询车辆最后订单的状态
    @Override
    public CarOrderEntity selectCarOrderStatus(Long communityId, String plateNum,Integer type) {
        //查询订单状态状态0未缴费1缴费
        QueryWrapper<CarOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("community_id",communityId);//社区id
        queryWrapper.eq("type",1);//临时车
        queryWrapper.eq("car_plate",plateNum);//车牌号
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        CarOrderEntity carOrderEntity = carOrderMapper.selectOne(queryWrapper);

        return carOrderEntity;
    }
}
