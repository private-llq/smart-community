package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarPatternService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPatternEntity;
import com.jsy.community.mapper.CarPatternMapper;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@DubboService(version = Const.version, group = Const.group_property)
@Slf4j
public class CarPatternServiceImpl extends ServiceImpl<CarPatternMapper, CarPatternEntity> implements ICarPatternService {
    @Autowired
    private  CarPatternMapper carPatternMapper;

    @Override
    public List<CarPatternEntity> listPattern(Long communityId) {
        List<CarPatternEntity> patternEntityList = carPatternMapper.selectList(new QueryWrapper<CarPatternEntity>().eq("community_id",communityId));
        System.out.println(patternEntityList);
        return patternEntityList;
    }

    /**
     * @Description: 查找单条
     * @Param: [patternId]
     * @Return: com.jsy.community.entity.property.CarPatternEntity
     * @Author: Tian
     * @Date: 2021/8/9-14:53
     **/
    @Override
    public CarPatternEntity findOne(String patternId) {
        QueryWrapper<CarPatternEntity> queryWrapper = new QueryWrapper<CarPatternEntity>().eq("pattern_id", patternId);
        CarPatternEntity carPatternEntity = carPatternMapper.selectOne(queryWrapper);
        return carPatternEntity;
    }

    @Override
    public boolean addPattern(String locationPattern,Long communityId) {
        CarPatternEntity carPatternEntity = new CarPatternEntity();
        carPatternEntity.setPatternId(UUID.randomUUID().toString());
        carPatternEntity.setCommunityId(communityId);
        carPatternEntity.setLocationPattern(locationPattern);
        return  carPatternMapper.insert(carPatternEntity) == 1;
    }

    @Override
    public boolean updatePattern(String locationPattern, String patternId, Long adminCommunityId) {
        QueryWrapper<CarPatternEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pattern_id",patternId)
                .eq("community_id",adminCommunityId);
        CarPatternEntity carPatternEntity = new CarPatternEntity();
        carPatternEntity.setLocationPattern(locationPattern);
       return carPatternMapper.update(carPatternEntity,queryWrapper)==1;
    }

    @Override
    public boolean deletePattern(String patternId, Long adminCommunityId) {
        QueryWrapper<CarPatternEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pattern_id",patternId)
                .eq("community_id",adminCommunityId);
        return carPatternMapper.delete(queryWrapper) == 1;
    }
}
