package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.VisitorEntity;
import org.apache.ibatis.annotations.MapKey;

import java.util.Collection;
import java.util.Map;

/**
 * @author chq459799974
 * @description 物业端访客Mapper
 * @since 2020-4-12 13:34
 **/
public interface VisitorMapper extends BaseMapper<VisitorEntity> {
	
	/**
	 * 批量查询访客登记
	 */
	@MapKey("id")
	Map<Long, VisitorEntity> queryVisitorMapBatch(Collection<Long> ids);
	
}
