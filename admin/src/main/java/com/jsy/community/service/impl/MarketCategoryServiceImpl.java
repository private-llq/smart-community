package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.mapper.MarketCategoryMapper;
import com.jsy.community.service.IMarketCategoryService;
import com.jsy.community.utils.SnowFlake;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class MarketCategoryServiceImpl extends ServiceImpl<MarketCategoryMapper, ProprietorMarketCategoryEntity> implements IMarketCategoryService {
    
    @Resource
    private MarketCategoryMapper categoryMapper;

    /**
     * @Description: 新增社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @author: DKS
     * @since: 2021/11/2 11:08
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
     * @author: DKS
     * @since: 2021/11/2 11:51
     **/
    @Override
    public boolean updateMarketCategory(ProprietorMarketCategoryEntity categoryEntity) {
        return categoryMapper.updateById(categoryEntity) == 1;
    }
    
    /**
     * @Description: 删除社区集市商品类别
     * @author: DKS
     * @since: 2021/11/2 11:54
     * @Param: [id]
     * @return: boolean
     */
    @Override
    public boolean deleteMarketCategory(Long id) {
        return categoryMapper.delete(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("id",id)) == 1;
    }
    
    /**
     * @Description: 查询社区集市商品类别列表
     * @author: DKS
     * @since: 2021/11/2 13:58
     * @Param: []
     * @return: java.util.List<com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity>
     */
    @Override
    public List<ProprietorMarketCategoryEntity> selectMarketCategory() {
        return categoryMapper.selectList(new QueryWrapper<ProprietorMarketCategoryEntity>().orderByAsc("sort"));
    }
}
