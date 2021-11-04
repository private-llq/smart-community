package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;

import java.util.List;

public interface IMarketCategoryService extends IService<ProprietorMarketCategoryEntity>{
    /**
     * @Description: 新增社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @author: DKS
     * @since: 2021/11/2 11:08
     **/
    boolean addMarketCategory(ProprietorMarketCategoryEntity categoryEntity);

    /**
     * @Description: 修改社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @author: DKS
     * @since: 2021/11/2 11:51
     **/
    boolean updateMarketCategory(ProprietorMarketCategoryEntity categoryEntity);

    /**
     * @Description: 删除社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @author: DKS
     * @since: 2021/11/2 11:54
     **/
    boolean deleteMarketCategory(Long id);

    /**
     * @Description: 查询社区集市商品类别列表
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @author: DKS
     * @since: 2021/11/2 11:57
     **/
    List<ProprietorMarketCategoryEntity> selectMarketCategory();
}
