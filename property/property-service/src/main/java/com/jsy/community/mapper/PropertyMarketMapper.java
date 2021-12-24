package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.qo.property.PropertyMarketQO;
import com.jsy.community.qo.proprietor.ProprietorMarketQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PropertyMarketMapper extends BaseMapper<ProprietorMarketEntity> {
//    /**
//     * @Description: 分页查询发布商品
//     * @Param: [page, size, query]
//     * @Return: java.util.List<com.jsy.community.entity.proprietor.ProprietorMarketEntity>
//     * @Author: Tian
//     * @Date: 2021/8/21-16:37
//     **/
//    List<ProprietorMarketEntity> selectMarketPage(@Param("page") Long page, @Param("size") Long size, @Param("query") ProprietorMarketEntity query);
//
//    /**
//     * @Description: 发布商品条数
//     * @Param: [query]
//     * @Return: java.lang.Integer
//     * @Author: Tian
//     * @Date: 2021/8/21-16:37
//     **/
//    Long findTotal(@Param("query") ProprietorMarketEntity query);


    /**
     * @Description: 查询所有发布的商品
     * @Param: [page, size]
     * @Return: java.util.List<com.jsy.community.entity.proprietor.ProprietorMarketEntity>
     * @Author: Tian
     * @Date: 2021/8/23-10:48
     **/
    List<ProprietorMarketEntity> selectMarketAllPage(
            @Param("query") PropertyMarketQO query,
            @Param("page") Long page,
            @Param("size") Long size,
            @Param("uidSet") Collection<?> uidSet
            );

    /**
     * @Description: 查询所有发布的商品的条数
     * @Param: []
     * @Return: java.lang.Long
     * @Author: Tian
     * @Date: 2021/8/23-10:50
     *
     * @param query*/
    Long findTotals(@Param("query") PropertyMarketQO query, @Param("uidSet") Collection<?> uidSet);

//    /**
//     * @Description: 热门商品
//     * @Param: [baseQO]
//     * @Return: java.util.Map<java.lang.String,java.lang.Object>
//     * @Author: Tian
//     * @Date: 2021/8/26-14:30
//     **/
//    List<ProprietorMarketQO> selectMarketLikePage(@Param("page") Long page, @Param("size") Long size);
//    /**
//     * @Description: 热门商品条数
//     * @Param: [baseQO]
//     * @Return: java.util.Map<java.lang.String,java.lang.Object>
//     * @Author: Tian
//     * @Date: 2021/8/26-14:30
//     **/
//    Long findLikeTotals();

    /**
     * @Description: 查询黑名单
     * @Param: [page1, size]
     * @Return: java.util.List<com.jsy.community.entity.proprietor.ProprietorMarketEntity>
     * @Author: Tian
     * @Date: 2021/9/8-17:52
     **/
    List<ProprietorMarketEntity> selectMarketBlacklist(@Param("page") Long page, @Param("size") Long size);

    Long findCount();
    /**
     * @Description: 黑名单删除  大删除
     * @Param: [id]
     * @Return: boolean
     * @Author: Tian
     * @Date: 2021/9/18-10:27
     **/
    Long deleteBlacklist(@Param("id") Long id);
}
