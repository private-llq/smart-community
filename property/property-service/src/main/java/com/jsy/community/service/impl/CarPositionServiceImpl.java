package com.jsy.community.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.mapper.CarPositionMapper;
import com.jsy.community.qo.property.InsterCarPositionQO;
import com.jsy.community.qo.property.MoreInsterCarPositionQO;
import com.jsy.community.qo.property.SelectCarPositionPagingQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.management.JMException;
import java.util.List;

/**
 * <p>
 * 车位 服务实现类
 * </p>
 *
 * @author Arli
 * @since 2021-08-03
 */
@DubboService(version = Const.version, group = Const.group_property)
public class CarPositionServiceImpl extends ServiceImpl<CarPositionMapper, CarPositionEntity> implements ICarPositionService {
    @Resource
    private CarPositionMapper carPositionMapper;

    @Override
    public List<CarPositionEntity> selectCarPostionBystatustatus() {
        QueryWrapper<CarPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_pos_status", 1);
        List<CarPositionEntity> carPositionEntities = carPositionMapper.selectList(queryWrapper);
        return carPositionEntities;
    }

    @Override
    public Page<CarPositionEntity> selectCarPositionPaging(SelectCarPositionPagingQO qo, Long adminCommunityId) {
        QueryWrapper<CarPositionEntity> community = null;
        Page<CarPositionEntity> page = new Page<>(qo.getPage(), qo.getSize());
        QueryWrapper<CarPositionEntity> queryWrapper = new QueryWrapper<CarPositionEntity>().eq("community_id", adminCommunityId);

        if (qo.getCarPositionStatus() != null) {
            queryWrapper.eq("car_pos_status", qo.getCarPositionStatus());
        }
        if (qo.getCarPositionTypeId() != null) {
            queryWrapper.eq("type_id", qo.getCarPositionTypeId());
        }
        if (qo.getBindingStatus() != null) {
            queryWrapper.eq("binding_status", qo.getBindingStatus());
        }
        if (qo.getCarNumber()!= null) {
            queryWrapper.like("car_position",qo.getCarNumber());
        }
        Page<CarPositionEntity> carPositionEntityPage = carPositionMapper.selectPage(page, queryWrapper);
        return carPositionEntityPage;
    }

    @Override
    public <T> void seavefile(List<T> list) {

        carPositionMapper.seavefile(list);

    }

    @Override
    public List<CarPositionEntity> selectCarPosition(CarPositionEntity qo) {
        List<CarPositionEntity> carPositionEntity = carPositionMapper.selectCarPosition(qo);

        return carPositionEntity;
    }

    @Override
    public List<CarPositionEntity> getAll(Long adminCommunityId) {
        QueryWrapper<CarPositionEntity> queryWrapper = new QueryWrapper<CarPositionEntity>()
                .eq("community_id", adminCommunityId)
                .eq("binding_status", 0)
                .eq("car_pos_status", 0);

        List<CarPositionEntity> list = carPositionMapper.selectList(queryWrapper);

        return list;
    }

    @Override
    public Boolean insterCarPosition(InsterCarPositionQO qo, Long adminCommunityId) {

        List<CarPositionEntity> list = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().eq("car_position", qo.getCarPosition()));
        if (list.size()>0) {
            throw  new PropertyException(500,"车位号已经存在");
        }

        CarPositionEntity carPositionEntity = new CarPositionEntity();
        BeanUtils.copyProperties(qo, carPositionEntity);
        carPositionEntity.setCommunityId(adminCommunityId);
        int insert = carPositionMapper.insert(carPositionEntity);
        if (insert > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean moreInsterCarPosition(MoreInsterCarPositionQO qo, Long adminCommunityId) {
        Integer number = qo.getNumber();//数量
        if (number < 0) {
            throw new PropertyException(500, "数量不能小于0");
        }


        Integer start = qo.getStart();//开始号码
        Integer x = 0;

        for (Integer n = start; n < start + number; n++) {

            String s = n.toString();
            int m = n.toString().length();
            if (m == 1) {
                s = "0000" + s;
            } else if (m == 2) {
                s = "000" + s;
            } else if (m == 3) {
                s = "00" + s;
            } else if (m == 4) {
                s = "0" + s;
            }
            CarPositionEntity carPositionEntity = new CarPositionEntity();
            BeanUtils.copyProperties(qo, carPositionEntity);
            carPositionEntity.setCarPosition(s);
            carPositionEntity.setCommunityId(adminCommunityId);
            int insert = carPositionMapper.insert(carPositionEntity);
            if (insert > 0) {
                x++;
            }
        }

        if (x == number) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean relieve(Long id) {
//        CarPositionEntity carPositionEntity = new CarPositionEntity();
//        carPositionEntity.setId(id);
//        carPositionEntity.setBeginTime(null);
//        carPositionEntity.setEndTime(null);
//        carPositionEntity.setCarPosStatus(0);
//        carPositionEntity.setBindingStatus(0);
//        carPositionEntity.setUid(null);
//        carPositionEntity.setBelongHouse(null);
//        carPositionEntity.setOwnerPhone(null);
//        carPositionEntity.setUserName(null);
//        int i = carPositionMapper.updateById(carPositionEntity);

        int i =   carPositionMapper.relieve(id);


        if (i>0){
            return true;
        }

        return false;
    }

    @Override
    public Boolean deletedCarPosition(Long id) {
        CarPositionEntity carPositionEntity = carPositionMapper.selectById(id);

        Integer bindingStatus = carPositionEntity.getBindingStatus();
        if (bindingStatus==1) {
            throw new PropertyException(500,"已经绑定不能删除");
        }
        int i = carPositionMapper.deleteById(id);
        if (i>0){
            return true;
        }
        return false;

    }

}
