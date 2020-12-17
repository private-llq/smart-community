package com.jsy.community.api;


import com.jsy.community.entity.HouseEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author chq459799974
 * @description 楼栋房屋
 * @since 2020-12-16 14:12
 **/
public interface IHouseService {
	
	List<HouseEntity> queryHouses(Collection<Long> list);
	
	HouseEntity getParent(HouseEntity tempEntity);
	
}
