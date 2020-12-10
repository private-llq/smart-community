package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitingCarEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description 随行车辆Mapper接口
 * @since 2020-11-12
 */
public interface VisitingCarMapper extends BaseMapper<VisitingCarEntity> {
	/**
	 * @Description: 批量新增随行车辆
	 * @Param: [list]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	int addCarBatch(List<VisitingCarEntity> list);
}
