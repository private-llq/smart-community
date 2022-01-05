package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;
import com.jsy.community.mapper.MarketCategoryMapper;
import com.jsy.community.mapper.MarketMapper;
import com.jsy.community.service.AdminException;
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
    
    @Resource
    private MarketMapper marketMapper;

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
        // 查询是否存在该顺序的商品类别
        ProprietorMarketCategoryEntity sort = categoryMapper.selectOne(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("sort", categoryEntity.getSort()).eq("deleted", 0));
        if (sort != null) {
            // 修改大于等于该顺序的
            categoryMapper.updateSort(categoryEntity.getSort());
        }
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
        // 查询是否存在该顺序的商品类别
        ProprietorMarketCategoryEntity sort = categoryMapper.selectOne(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("sort", categoryEntity.getSort()).eq("deleted", 0));
        if (sort != null) {
            // 修改大于等于该顺序的
            categoryMapper.updateSort(categoryEntity.getSort());
        }
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
        ProprietorMarketCategoryEntity proprietorMarketCategoryEntity = categoryMapper.selectById(id);
        // 根据分类id查询是否存在商品
        if (proprietorMarketCategoryEntity != null) {
            Integer integer = marketMapper.selectMarketByCategoryId(proprietorMarketCategoryEntity.getCategoryId());
            if (integer > 0) {
                throw new AdminException("存在商品的分类无法被删除");
            } else {
                return categoryMapper.delete(new QueryWrapper<ProprietorMarketCategoryEntity>().eq("id",id)) == 1;
            }
        } else {
            throw new AdminException("不存在该集市商品分类");
        }
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
