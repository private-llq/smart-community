package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 社区楼栋 Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-11-20
 */
public interface HouseMapper extends BaseMapper<HouseEntity> {
	
	List<Long> getSubIdList(List<Long> list);
	
	int addHouse(@Param("houseEntity") HouseEntity houseEntity);

}
