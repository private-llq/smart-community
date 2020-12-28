package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.shop.ShopImgEntity;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
public interface ShopImgMapper extends BaseMapper<ShopImgEntity> {
	
	/**
	 * @return void
	 * @Author lihao
	 * @Description 存店铺图片集合
	 * @Date 2020/12/17 14:20
	 * @Param [imgPath]
	 **/
	void insertImg(List<ShopImgEntity> list);
}
