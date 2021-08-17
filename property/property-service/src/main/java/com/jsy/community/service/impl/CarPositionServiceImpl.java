package com.jsy.community.service.impl;



import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.mapper.CarPositionMapper;
import com.jsy.community.qo.property.SelectCarPositionPagingQO;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
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
    private     CarPositionMapper carPositionMapper;

    @Override
    public List<CarPositionEntity> selectCarPostionBystatustatus() {
        QueryWrapper<CarPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_pos_status",1);
        List<CarPositionEntity> carPositionEntities = carPositionMapper.selectList(queryWrapper);
        return carPositionEntities;
    }

    @Override
    public Page<CarPositionEntity> selectCarPositionPaging(SelectCarPositionPagingQO qo, Long adminCommunityId) {
        QueryWrapper<CarPositionEntity> community=null;
        Page<CarPositionEntity> page = new Page<>(qo.getPage(), qo.getSize());

        if(ObjectUtil.isNull(qo.getCarPositionStatus()) & ObjectUtil.isNull(qo.getCarPositionTypeId()) & ObjectUtil.isNull(qo.getBindingStatus())) {
           community = new QueryWrapper<CarPositionEntity>().eq("community_id", adminCommunityId);
        }
        if(ObjectUtil.isNotNull(qo.getCarPositionStatus()) & ObjectUtil.isNull(qo.getCarPositionTypeId()) & ObjectUtil.isNull(qo.getBindingStatus())) {
            community = new QueryWrapper<CarPositionEntity>().eq("community_id", adminCommunityId).eq("car_pos_status",qo.getCarPositionStatus());
        }
        if(ObjectUtil.isNull(qo.getCarPositionStatus()) & ObjectUtil.isNotNull(qo.getCarPositionTypeId()) & ObjectUtil.isNull(qo.getBindingStatus())) {
            community = new QueryWrapper<CarPositionEntity>().eq("community_id", adminCommunityId).eq("type_id",qo.getCarPositionTypeId());
        }
        if(ObjectUtil.isNull(qo.getCarPositionStatus()) & ObjectUtil.isNull(qo.getCarPositionTypeId()) & ObjectUtil.isNotNull(qo.getBindingStatus())) {
            community = new QueryWrapper<CarPositionEntity>().eq("community_id", adminCommunityId).eq("binding_status",qo.getBindingStatus());
        }

        Page<CarPositionEntity> carPositionEntityPage = carPositionMapper.selectPage(page, community);

        return carPositionEntityPage;
    }

    @Override
    public <T> void seavefile(List<T> list) {

        carPositionMapper.seavefile(list);

    }

    @Override
    public List<CarPositionEntity> selectCarPosition(CarPositionEntity qo) {
        List<CarPositionEntity>   carPositionEntity= carPositionMapper.selectCarPosition(qo);

        return carPositionEntity;
    }

    @Override
    public List<CarPositionEntity> getAll(Long adminCommunityId) {
        List<CarPositionEntity> list = carPositionMapper.selectList(new QueryWrapper<CarPositionEntity>().eq("community_id", adminCommunityId));

        return list;
    }

}
