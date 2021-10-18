package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopLeaseMapper extends BaseMapper<ShopLeaseEntity> {

    @Select("select img_url from t_shop_img where shop_id = #{shopId}")
    List<String> queryAllShopImg(@Param("shopId") Long shopId);

}
