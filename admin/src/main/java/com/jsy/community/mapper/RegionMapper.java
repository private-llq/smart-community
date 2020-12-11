package com.jsy.community.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 省市区表Mapper
 * @author chq459799974
 * @since 2020-11-13
 */
@Mapper
public interface RegionMapper {
	
	List<Long> getPayTypeIds(Long id);
	
	void insertMiddle(Integer id, Long id1);
}
