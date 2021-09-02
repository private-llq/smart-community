package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IProprietorMarketCategoryService;
import com.jsy.community.api.IProprietorMarketService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.mapper.ProprietorMarketCategoryMapper;
import com.jsy.community.mapper.ProprietorMarketCategoryMapper;
import com.jsy.community.mapper.ProprietorMarketMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@DubboService(version = Const.version, group = Const.group_proprietor)
public class ProprietorMarketCategoryServiceImpl extends ServiceImpl<ProprietorMarketCategoryMapper, ProprietorMarketCategoryEntity> implements IProprietorMarketCategoryService {
    @Autowired
    private ProprietorMarketCategoryMapper categoryMapper;

    /**
     * @Description: 新增社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:43
     **/
    @Override
    public boolean addMarketCategory(ProprietorMarketCategoryEntity categoryEntity) {
        categoryEntity.setId(SnowFlake.nextId());
        categoryEntity.setCategoryId(UUID.randomUUID().toString());
        return categoryMapper.insert(categoryEntity) == 1;
    }

    /**
     * @Description: 修改社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:50
     **/
    @Override
    public boolean updateMarketCategory(ProprietorMarketCategoryEntity categoryEntity) {
        return categoryMapper.update(categoryEntity,new UpdateWrapper<ProprietorMarketCategoryEntity>().eq("id",categoryEntity.getId())) == 1;
    }

    @Override
    public boolean deleteMarketCategory(Long id) {

        return categoryMapper.delete(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("id",id)) == 1;
    }

    @Override
    public List<ProprietorMarketCategoryEntity> selectMarketCategory() {
        return categoryMapper.selectList(new QueryWrapper<ProprietorMarketCategoryEntity>().orderByDesc("update_time"));
    }

    @Override
    public ProprietorMarketCategoryEntity findOne(String categoryId) {
        ProprietorMarketCategoryEntity categoryEntity = categoryMapper.selectOne(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("category_id", categoryId));
        return  categoryEntity;
    }
}
