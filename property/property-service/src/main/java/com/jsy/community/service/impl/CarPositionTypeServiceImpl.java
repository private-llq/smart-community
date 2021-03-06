package com.jsy.community.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarPositionTypeService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPositionTypeEntity;
import com.jsy.community.mapper.CarPositionMapper;
import com.jsy.community.mapper.CarPositionTypeMapper;
import com.jsy.community.qo.property.UpdateCartPositionTypeQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.property.SelectCartPositionTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 车位类型表 服务实现类
 * </p>
 *
 * @author Arli
 * @since 2021-08-05
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarPositionTypeServiceImpl extends ServiceImpl<CarPositionTypeMapper, CarPositionTypeEntity> implements ICarPositionTypeService {
    @Resource
    private     CarPositionTypeMapper carPositionTypeMapper;

    @Override
    @Transactional
    public Boolean insterCartPositionType(String description ,Long CommunityId) {

        CarPositionTypeEntity entity=new CarPositionTypeEntity();
        entity.setDescription(description);
        entity.setCommunityId(CommunityId);
        entity.setTypeId(UUID.randomUUID().toString());
        QueryWrapper<CarPositionTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("description",description);
        queryWrapper.eq("community_id",CommunityId);
        List<CarPositionTypeEntity> list = carPositionTypeMapper.selectList(queryWrapper);
        if (list.size()>0) {
            log.info("该车位分类已经存在");
            throw new PropertyException(500,"分类已存在");
        }
        int insert = carPositionTypeMapper.insert(entity);

        if(insert>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCartPositionType(UpdateCartPositionTypeQO qo,Long adminCommunityId) {
        CarPositionTypeEntity entity=new CarPositionTypeEntity();
        entity.setDescription(qo.getDescription());
        entity.setId(qo.getId());
        QueryWrapper<CarPositionTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("description",qo.getDescription());
        queryWrapper.eq("community_id",adminCommunityId);
        List<CarPositionTypeEntity> list = carPositionTypeMapper.selectList(queryWrapper);
        if (list.size()>0) {
            log.info("该车位分类已经存在");
            throw new PropertyException(500,"分类已存在");
        }

        int i = carPositionTypeMapper.updateById(entity);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public List<SelectCartPositionTypeVO> selectCartPositionType(Long adminCommunityId) {
        List<CarPositionTypeEntity> community = carPositionTypeMapper.selectList(new QueryWrapper<CarPositionTypeEntity>().eq("community_id", adminCommunityId));
        List<SelectCartPositionTypeVO> list= new ArrayList<>();
        for (CarPositionTypeEntity entity : community) {
            SelectCartPositionTypeVO vo=new SelectCartPositionTypeVO();
            BeanUtils.copyProperties(entity,vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public Boolean deleteCartPositionType(String id) {
        int i = carPositionTypeMapper.deleteById(id);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public List<CarPositionTypeEntity> getAllType() {
        List<CarPositionTypeEntity> list = carPositionTypeMapper.selectList(null);
        return list;
    }
}
