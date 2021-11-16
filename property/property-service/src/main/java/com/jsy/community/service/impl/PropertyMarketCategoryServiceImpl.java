package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyMarketCategoryService;
import com.jsy.community.api.IProprietorMarketCategoryService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.mapper.PropertyMarketCategoryMapper;
import com.jsy.community.mapper.PropertyMarketMapper;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@DubboService(version = Const.version, group = Const.group_property)
public class PropertyMarketCategoryServiceImpl extends ServiceImpl<PropertyMarketCategoryMapper, ProprietorMarketCategoryEntity> implements IPropertyMarketCategoryService {
    @Autowired
    private PropertyMarketCategoryMapper categoryMapper;
    @Autowired
    private PropertyMarketMapper marketMapper;

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
        // 查询分类下是否含有有效商品,如果有,则提示不能删除
        QueryWrapper<ProprietorMarketEntity> marketEntityQueryWrapper = new QueryWrapper<>();
        marketEntityQueryWrapper.eq("state", 1);
        marketEntityQueryWrapper.last("limit 1");
        ProprietorMarketEntity proprietorMarketEntity = marketMapper.selectOne(marketEntityQueryWrapper);
        if (proprietorMarketEntity != null) {
            throw new PropertyException("该类别下含有代售商品,不能删除");
        }
        return categoryMapper.delete(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("id",id)) == 1;
    }

    @Override
    public List<ProprietorMarketCategoryEntity> selectMarketCategory() {
        return categoryMapper.selectList(new QueryWrapper<ProprietorMarketCategoryEntity>().orderByAsc("sort"));
    }

    @Override
    public ProprietorMarketCategoryEntity findOne(String categoryId) {
        ProprietorMarketCategoryEntity categoryEntity = categoryMapper.selectOne(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("category_id", categoryId));
        return  categoryEntity;
    }
}
