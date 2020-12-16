package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserHouseEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description 楼栋房间
 * @since 2020-12-16 13:41
 **/
public interface HouseMapper extends BaseMapper<HouseEntity> {
	
	List<UserHouseEntity> queryUserHouses(List<Long> list);
	
}
