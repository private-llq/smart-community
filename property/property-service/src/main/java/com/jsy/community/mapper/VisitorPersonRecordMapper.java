package com.jsy.community.mapper;


import org.apache.ibatis.annotations.MapKey;

import java.util.Collection;
import java.util.Map;

/**
 * @author chq459799974
 * @description 物业端 随行人员记录Mapper接口
 * @since 2021-04-12 14:07
 **/
public interface VisitorPersonRecordMapper {
	
	/**
	 * 批量查询访客随行人数统计
	 */
	@MapKey("visitor_id")
	Map<Long, Map<Long,Long>> getFollowPersonBatch(Collection ids);
	
}
