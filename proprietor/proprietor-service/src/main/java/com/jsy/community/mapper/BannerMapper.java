package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.BannerEntity;
import org.apache.ibatis.annotations.Update;

/**
 * banner轮播图 Mapper 接口
 *
 * @author chq459799974
 * @since 2020-11-16
 */
public interface BannerMapper extends BaseMapper<BannerEntity> {
	
	/**
	* @Description: 点击量+1
	 * @Param: [id]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/29
	**/
	@Update("update t_banner set click = click + 1 where id = #{id}")
	int clickUp(Long id);
	
}
