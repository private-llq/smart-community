package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.proprietor.ProprietorMarketCategoryEntity;

import java.util.List;

public interface IPropertyMarketCategoryService extends IService<ProprietorMarketCategoryEntity>{
    /**
     * @Description: 新增社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:40
     **/
    boolean addMarketCategory(ProprietorMarketCategoryEntity categoryEntity);

    /**
     * @Description: 修改社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    boolean updateMarketCategory(ProprietorMarketCategoryEntity categoryEntity);

    /**
     * @Description: 删除社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    boolean deleteMarketCategory(Long id);

    /**
     * @Description: 查询社区集市商品类别
     * @Param: [CategoryEntity]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/8/21-9:44
     **/
    List<ProprietorMarketCategoryEntity> selectMarketCategory();

    ProprietorMarketCategoryEntity findOne(String categoryId);
}
