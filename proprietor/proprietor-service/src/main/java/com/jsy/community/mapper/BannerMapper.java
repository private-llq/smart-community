package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.BannerEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * banner轮播图 Mapper 接口
 *
 * @author chq459799974
 * @since 2020-11-16
 */
public interface BannerMapper extends BaseMapper<BannerEntity> {
	
	/**
	* @Description: 刷新点击量
	 * @Param: [map]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2020/12/30
	**/
	void refreshClickCount(@Param("map") Map<Long,Long> map);
	
}
