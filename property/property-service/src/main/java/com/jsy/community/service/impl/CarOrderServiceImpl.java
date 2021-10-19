package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarOrderService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.mapper.CarOrderMapper;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import java.time.LocalDateTime;


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
        queryWrapper.eq("type",type);//临时车
        queryWrapper.eq("car_plate",plateNum);//车牌号
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        CarOrderEntity carOrderEntity = carOrderMapper.selectOne(queryWrapper);

        return carOrderEntity;
    }
    //删除未支付的订单
    @Override
    public void deletedNOpayOrder(String plateNum, Long communityId, LocalDateTime beginTime) {
        QueryWrapper<CarOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("community_id",communityId);
        wrapper.eq("car_plate",plateNum);//车牌号
        wrapper.eq("begin_time",beginTime);//进闸时间
        wrapper.eq("order_status",0);//未支付
        wrapper.eq("type",1);//临时车
        int delete = carOrderMapper.delete(wrapper);
        System.out.println("删除情况");
    }

    //根据订单号查询订单记录
    @Override
    public CarOrderEntity selectOneOrder(String orderNumber){
        CarOrderEntity entity = carOrderMapper.selectOne(new QueryWrapper<CarOrderEntity>().eq("order_num", orderNumber));
        return entity;
    }

    //修改订单
    @Override
    public  boolean updateOrder(CarOrderEntity carOrderEntity,String orderNumber){
        UpdateWrapper<CarOrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_num",orderNumber);
        int update = carOrderMapper.update(carOrderEntity, updateWrapper);
        if(update>0){
            return true;
        }
        return false;

    }


    public CarOrderEntity selectId(Long id) {
        CarOrderEntity entity = carOrderMapper.selectOne(new QueryWrapper<CarOrderEntity>().eq("id", id));
        return entity;
    }


    public boolean updateOrderId(CarOrderEntity entity, long parseLong) {
        System.out.println(entity);
        UpdateWrapper<CarOrderEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",parseLong);
        int update = carOrderMapper.update(entity, updateWrapper);
        if(update>0){
            return true;
        }
        return false;
    }


}
