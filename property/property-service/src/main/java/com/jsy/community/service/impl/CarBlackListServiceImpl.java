package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarBlackListService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.property.CarBlackListEntity;
import com.jsy.community.mapper.CarBlackListMapper;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@DubboService(version = Const.version, group = Const.group_property)
public class CarBlackListServiceImpl  extends ServiceImpl<CarBlackListMapper, CarBlackListEntity> implements ICarBlackListService  {

    @Autowired
    public CarBlackListMapper carBlackListMapper;


    /**
     * 分页查询 黑名单
     * @param baseQO 车牌号
     * @return
     */
    @Override
    public PageInfo<CarBlackListEntity> carBlackListPage(BaseQO<String> baseQO,Long communityId) {
        Page<CarBlackListEntity> Page = new Page<>(baseQO.getPage(),baseQO.getSize());
        PageInfo<CarBlackListEntity> pageInfo = new PageInfo<>();
        Page<CarBlackListEntity> selectPage = carBlackListMapper.selectPage(Page,
                new QueryWrapper<CarBlackListEntity>()
                        .like(StringUtils.isNoneBlank(baseQO.getQuery()),"car_number", baseQO.getQuery())
                        .eq("community_id",communityId)
        );
        pageInfo.setTotal(selectPage.getTotal());
        pageInfo.setCurrent(selectPage.getCurrent());
        pageInfo.setSize(selectPage.getSize());
        pageInfo.setRecords(selectPage.getRecords());
        return pageInfo;
    }

    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @Override
    @Transactional
    public Integer delBlackList(String uid){
        int delete = carBlackListMapper.delete(new QueryWrapper<CarBlackListEntity>().eq("uid", uid));
        return delete;
    }

    @Override
    public CarBlackListEntity carBlackListOne(String carNumber ,Long communityId) {
        CarBlackListEntity carBlackListEntity = carBlackListMapper.selectOne(new QueryWrapper<CarBlackListEntity>().
                eq("car_number", carNumber).eq("community_id",communityId)
                );
        return carBlackListEntity;
    }

    /**
     * 添加进入黑名单
     * @param carBlackListEntity
     * @return
     */
    @Override
    @Transactional
    public Integer saveBlackList(CarBlackListEntity carBlackListEntity,Long communityId) {

        CarBlackListEntity car_number = carBlackListMapper.selectOne(new QueryWrapper<CarBlackListEntity>().eq("car_number", carBlackListEntity.getCarNumber()));
        if (Objects.nonNull(car_number)){
            throw new PropertyException("该车辆已被加入黑名单，请勿重复添加！");
        }
        carBlackListEntity.setUid(UserUtils.randomUUID());
        carBlackListEntity.setAddTime(LocalDateTime.now());
        carBlackListEntity.setCommunityId(communityId);
        int insert = carBlackListMapper.insert(carBlackListEntity);
        return insert;
    }


}
