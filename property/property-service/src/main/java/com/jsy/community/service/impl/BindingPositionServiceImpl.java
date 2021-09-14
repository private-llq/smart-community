package com.jsy.community.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IBindingPositionService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.BindingPositionEntity;
import com.jsy.community.entity.property.CarMonthlyVehicle;
import com.jsy.community.mapper.BindingPositionMapper;
import com.jsy.community.mapper.CarMonthlyVehicleMapper;
import com.jsy.community.utils.UserUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@DubboService(version = Const.version,group = Const.group_property)
public class BindingPositionServiceImpl extends ServiceImpl<BindingPositionMapper, BindingPositionEntity> implements IBindingPositionService {
    @Autowired
    private BindingPositionMapper bindingPositionMapper;

    @Autowired
    private CarMonthlyVehicleMapper carMonthlyVehicleMapper;


    /**
     * 新增未绑定状态的车辆
     */
    @Override
    @Transactional
    public Integer saveBinding(BindingPositionEntity bindingPositionEntity) {

        List<BindingPositionEntity> carList = bindingPositionMapper.selectList(new QueryWrapper<BindingPositionEntity>()
                .eq("community_id", bindingPositionEntity.getCommunityId())
                .eq("position_id", bindingPositionEntity.getPositionId())
                .eq("car_number",bindingPositionEntity.getCarNumber())
        );
        if (carList.size()!=0){
            throw new PropertyException("该车辆已添加，请勿重复添加！");
        }

        List<BindingPositionEntity> list = bindingPositionMapper.selectList(new QueryWrapper<BindingPositionEntity>()
                .eq("community_id", bindingPositionEntity.getCommunityId())
                .eq("position_id", bindingPositionEntity.getPositionId()));


        if (list.size()==0){
            bindingPositionEntity.setBindingStatus(1);//默认第一个添加进来的为绑定状态
        }else {
            bindingPositionEntity.setBindingStatus(0);//未绑定状态
        }

        bindingPositionEntity.setUid(UserUtils.randomUUID());
        int insert = bindingPositionMapper.insert(bindingPositionEntity);
        return insert;
    }

    /**
     * 查询该车位下面所有的车辆信息 包含已绑定 和 未绑定
     */
    @Override
    public List<BindingPositionEntity> selectBinding(BindingPositionEntity bindingPositionEntity) {
        List<BindingPositionEntity> bindingPositionEntities = bindingPositionMapper.
                selectList(new QueryWrapper<BindingPositionEntity>()
                        .eq("community_id",bindingPositionEntity.getCommunityId())
                        .eq("position_id",bindingPositionEntity.getPositionId())
                );
        return bindingPositionEntities;
    }




    /**
     * 包月车辆换绑车位
     */
    @Override
    @Transactional
    public void binding(BindingPositionEntity bindingPositionEntity) {
        String carNumber = bindingPositionEntity.getCarNumber();//车牌号
        Long communityId = bindingPositionEntity.getCommunityId();//社区id
        String positionId = bindingPositionEntity.getPositionId();//车位id
        String uid = bindingPositionEntity.getUid();

        BindingPositionEntity positionEntity = bindingPositionMapper.selectOne(new QueryWrapper<BindingPositionEntity>()
                .eq("position_id", positionId)
                .eq("community_id", communityId)
                .eq("binding_status", 1)
        );
        if (Objects.nonNull(positionEntity)){
            BindingPositionEntity entity = new BindingPositionEntity();
            entity.setBindingStatus(0);//改为未绑定状态
            bindingPositionMapper.update(entity,new QueryWrapper<BindingPositionEntity>().eq("uid",positionEntity.getUid()));

        }
        //再根据uid去重新绑定一个车位
        BindingPositionEntity entity = new BindingPositionEntity();
        entity.setBindingStatus(1);
        bindingPositionMapper.update(entity,new QueryWrapper<BindingPositionEntity>().eq("uid",uid));

        //再根据车位号去修改包月表里面的车牌号
        CarMonthlyVehicle vehicle = new CarMonthlyVehicle();
        vehicle.setCarNumber(carNumber);
        carMonthlyVehicleMapper.update(vehicle,new QueryWrapper<CarMonthlyVehicle>().eq("car_position",positionId).eq("community_id",communityId));

    }

    @Override
    @Transactional
    public void deleteBinding(String uid) {
       /* BindingPositionEntity entity = bindingPositionMapper.selectOne(new QueryWrapper<BindingPositionEntity>().eq("uid", uid));
        if (entity.getBindingStatus()==1){
            throw new PropertyException("该车辆已绑定车位，如需删除请先解绑！");
        }*/
        bindingPositionMapper.delete(new QueryWrapper<BindingPositionEntity>().eq("uid",uid));
    }


}