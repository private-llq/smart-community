package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarChargeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarChargeEntity;
import com.jsy.community.mapper.CarChargeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DubboService(version = Const.version, group = Const.group)
public class CarChargeServiceImpl extends ServiceImpl<CarChargeMapper, CarChargeEntity> implements ICarChargeService {

    @Autowired
    public CarChargeMapper carChargeMapper;


    @Override
    @Transactional
    public Integer SaveCarCharge(CarChargeEntity carChargeEntity,Long communityId) {
        carChargeEntity.setCommunityId(communityId);
        carChargeEntity.setUid(UserUtils.randomUUID());
        int insert = carChargeMapper.insert(carChargeEntity);
        return insert;
    }

    @Override
    @Transactional
    public Integer UpdateCarCharge(CarChargeEntity carChargeEntity) {
        int update = carChargeMapper.update(carChargeEntity, new UpdateWrapper<CarChargeEntity>().eq("uid", carChargeEntity.getUid()));
        return update;
    }

    @Override
    @Transactional
    public Integer DelCarCharge(String uid) {
        int del = carChargeMapper.delete(new QueryWrapper<CarChargeEntity>().eq("uid", uid));
        return del;
    }

    @Override
    public List<CarChargeEntity> selectCharge(Integer type,Long communityId) {
        List<CarChargeEntity> list = carChargeMapper.selectList(new QueryWrapper<CarChargeEntity>()
                .eq("type", type)
                .eq("community_id",communityId)
        );
        return list;
    }

    /**
     * 根据类型查找并分页
     * @param baseQO
     * @return
     */
    @Override
    public PageInfo listCarChargePage(BaseQO baseQO,Long communityId) {
        Page<CarChargeEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        PageInfo<CarChargeEntity> pageInfo = new PageInfo<>();
        Page<CarChargeEntity> selectPage = carChargeMapper.selectPage(page,
                new QueryWrapper<CarChargeEntity>()
                        .eq(baseQO.getQuery()!=null,"type", baseQO.getQuery())
                        .eq("community_id",communityId)
        );
        pageInfo.setRecords(selectPage.getRecords());
        pageInfo.setTotal(selectPage.getTotal());
        pageInfo.setCurrent(selectPage.getCurrent());
        pageInfo.setSize(selectPage.getSize());
        return pageInfo;
    }
}






