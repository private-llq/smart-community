package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitingCarRecordEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description 随行车辆记录Mapper接口
 * @since 2020-12-10 13:54
 **/
public interface VisitingCarRecordMapper extends BaseMapper<VisitingCarRecordEntity> {
	/**
	 * @Description: 批量新增随行车辆记录
	 * @Param: [list]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	int addCarBatch(List<VisitingCarRecordEntity> list);
}
