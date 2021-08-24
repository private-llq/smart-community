package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.CarEntity;

import java.util.List;

/**
 * @ClassName：CarMapper
 * @Description：车辆mapper接口
 * @author：DKS
 * @date：2021/8/24 14:09
 * @version：1.0
 */
public interface CarMapper extends BaseMapper<CarEntity> {
	/**
	 *@Author: DKS
	 *@Description: 查询小区下所有的车辆
	 *@Param: communityId:
	 *@Return: java.util.List<com.jsy.community.entity.CarEntity>
	 *@Date: 2021/8/24 14:01
	 **/
	List<CarEntity> getAllCarByCommunity(Long communityId);
}
