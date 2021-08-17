package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.property.CarPatternEntity;
import com.jsy.community.entity.property.CarProprietorEntity;
import com.jsy.community.mapper.CarProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class CarProprietorServiceImpl extends ServiceImpl<CarProprietorMapper,CarProprietorEntity> implements ICarProprietorService {
    @Autowired
    private CarProprietorMapper carProprietorMapper;

    /**
     * @Description: 查询所有数据
     * @Param: [adminCommunityId]
     * @Return: java.util.List<com.jsy.community.entity.property.CarProprietorEntity>
     * @Author: Tian
     * @Date: 2021/8/11-10:37
     **/
    @Override
    public List<CarProprietorEntity> listAll(Long adminCommunityId) {
        List<CarProprietorEntity> list = carProprietorMapper.selectList(new QueryWrapper<CarProprietorEntity>().eq("community_id", adminCommunityId));
        return list;
    }

    /**
     * @Description: 分页查询数据
     * @Param: [baseQO, adminCommunityId]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.property.CarProprietorEntity>
     * @Author: Tian
     * @Date: 2021/8/11-10:36
     **/
    @Override
    public Page<CarProprietorEntity> listPage(BaseQO<CarProprietorEntity> baseQO, Long adminCommunityId, Long phone) {
        QueryWrapper<CarProprietorEntity> queryWrapper = new QueryWrapper<CarProprietorEntity>().eq("community_id", adminCommunityId);

        Page<CarProprietorEntity> page = new Page<CarProprietorEntity>();
        if (baseQO.getPage() ==0 ||baseQO.getPage()==null){
            baseQO.setPage(10l);
        }
        if (phone!=null){
            queryWrapper.like("phone",phone);
        }
        Page<CarProprietorEntity> selectPage = carProprietorMapper.selectPage(page, queryWrapper);

        return selectPage;

    }

    @Override
    public boolean addProprietor(CarProprietorEntity carProprietorEntity, Long adminCommunityId) {
        carProprietorEntity.setId(SnowFlake.nextId());
        carProprietorEntity.setCommunityId(adminCommunityId);
        return carProprietorMapper.insert(carProprietorEntity) == 1;
    }

    @Override
    public boolean updateProprietor(CarProprietorEntity carProprietorEntity) {
        return carProprietorMapper.update(carProprietorEntity,new QueryWrapper<CarProprietorEntity>().eq("id",carProprietorEntity.getId())) == 1;
    }

    @Override
    public boolean deleteProprietor(Long id) {
        return carProprietorMapper.delete(new QueryWrapper<CarProprietorEntity>().eq("id",id))== 1;
    }


}
