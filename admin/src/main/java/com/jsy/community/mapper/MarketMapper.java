package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.proprietor.ProprietorMarketEntity;
import com.jsy.community.qo.property.PropertyMarketQO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MarketMapper extends BaseMapper<ProprietorMarketEntity> {
    
    /**
     * @Description: 查询所有发布的商品
     * @Param: [page, size]
     * @Return: java.util.List<com.jsy.community.entity.proprietor.ProprietorMarketEntity>
     * @Author: DKS
     * @Date: 2021/11/1-10:44
     **/
    List<ProprietorMarketEntity> selectMarketAllPage(@Param("query") PropertyMarketQO query, @Param("page") Long page, @Param("size") Long size);

    /**
     * @Description: 查询所有发布的商品的条数
     * @Param: []
     * @Return: java.lang.Long
     * @Author: DKS
     * @Date: 2021/11/1-10:44
     *
     * @param query*/
    Long findTotals(@Param("query") PropertyMarketQO query);

    /**
     * @Description: 查询黑名单
     * @Param: [page1, size]
     * @Return: java.util.List<com.jsy.community.entity.proprietor.ProprietorMarketEntity>
     * @Author: DKS
     * @Date: 2021/11/1-11:00
     **/
    List<ProprietorMarketEntity> selectMarketBlacklist(@Param("page") Long page, @Param("size") Long size);

    /**
     * @Description: 查询黑名单条数
     * @author: DKS
     * @since: 2021/11/1 11:00
     * @Param: []
     * @return: java.lang.Long
     */
    Long findCount();
    
    /**
     * @Description: 黑名单删除  大删除
     * @Param: [id]
     * @Return: boolean
     * @Author: DKS
     * @Date: 2021/11/1-10:18
     **/
    Long deleteBlacklist(@Param("id") Long id);
    
    /**
     * @Description: 根据分类id查询是否存在商品
     * @author: DKS
     * @since: 2021/11/10 10:06
     * @Param: java.lang.String
     * @return: com.jsy.community.entity.proprietor.ProprietorMarketEntity
     */
    Integer selectMarketByCategoryId(String categoryId);
}
