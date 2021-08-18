
package com.jsy.community.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.CarLaneEntity;
import com.jsy.community.mapper.CarLaneMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@DubboService(version = Const.version, group = Const.group)
public class CarLaneServiceImpl extends ServiceImpl<CarLaneMapper, CarLaneEntity> implements ICarLaneService {

    @Autowired
    public CarLaneMapper carLaneMapper;


    @Override
    @Transactional
    public Integer SaveCarLane(CarLaneEntity CarLaneEntity,Long communityId) {
        CarLaneEntity.setUid((UserUtils.randomUUID()));
        CarLaneEntity.setCommunityId(communityId);
        int insert = carLaneMapper.insert(CarLaneEntity);
        return insert;
    }

    @Override
    @Transactional
    public Integer UpdateCarLane(CarLaneEntity carLaneEntity) {
        int Update = carLaneMapper.update(carLaneEntity, new UpdateWrapper<CarLaneEntity>().eq("uid", carLaneEntity.getUid()));
        return Update;
    }


    @Override
    @Transactional
    public Integer DelCarLane(String uid) {
        int del = carLaneMapper.delete(new QueryWrapper<CarLaneEntity>().eq("uid", uid));
        return del;
    }


    @Override
    public PageInfo FindByLaneNamePage(BaseQO<String> baseQO,Long communityId) {
        Page<CarLaneEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
        PageInfo<CarLaneEntity> pageInfo = new PageInfo<>();
        Page<CarLaneEntity> selectPage = carLaneMapper.selectPage(page,new QueryWrapper<CarLaneEntity>()
                        .like(StringUtils.isNoneBlank(baseQO.getQuery()),"lane_name",baseQO.getQuery())
                        .eq("community_id",communityId)
        );
        pageInfo.setRecords(selectPage.getRecords());
        pageInfo.setTotal(selectPage.getTotal());
        pageInfo.setCurrent(selectPage.getCurrent());
        pageInfo.setSize(selectPage.getSize());

        return pageInfo;
    }

}