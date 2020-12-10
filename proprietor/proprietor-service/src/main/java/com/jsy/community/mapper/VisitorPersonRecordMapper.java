package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitorPersonRecordEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description 随行人员记录Mapper接口
 * @since 2020-12-10 13:57
 **/
public interface VisitorPersonRecordMapper extends BaseMapper<VisitorPersonRecordEntity> {
	/**
	 * @Description: 批量新增随行人员记录
	 * @Param: [list]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/12/10
	 **/
	int addPersonBatch(List<VisitorPersonRecordEntity> list);
}
