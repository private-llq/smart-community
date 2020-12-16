package com.jsy.community.service.impl;

import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.HouseMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author chq459799974
 * @description 楼栋房屋
 * @since 2020-12-16 14:10
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class HouseServiceImpl implements IHouseService {
	
	@Autowired
	private HouseMapper houseMapper;
	
	@Override
	public List<UserHouseEntity> queryUserHouses(List<Long> list){
		return houseMapper.queryUserHouses(list);
	}
	
}
