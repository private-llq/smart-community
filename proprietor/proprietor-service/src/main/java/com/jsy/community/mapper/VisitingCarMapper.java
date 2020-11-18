package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitingCarEntity;

import java.util.List;

/**
 * <p>
 * 来访车辆 Mapper 接口
 * </p>
 *
 * @author jsy
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
