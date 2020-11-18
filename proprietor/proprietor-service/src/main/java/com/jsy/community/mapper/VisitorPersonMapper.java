package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitorPersonEntity;

import java.util.List;

/**
 * <p>
 * 访客随行人员 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
public interface VisitorPersonMapper extends BaseMapper<VisitorPersonEntity> {
	/**
	* @Description: 批量新增随行人员
	 * @Param: [list]
	 * @Return: int
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	int addPersonBatch(List<VisitorPersonEntity> list);
}