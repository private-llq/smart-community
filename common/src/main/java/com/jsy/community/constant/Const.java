package com.jsy.community.constant;

public interface Const {
	String version = "1.0";
	
	String group = "dev";
	
	interface HouseMemberConsts {
		Integer UNJOIN = 0;//未加入
		Integer JOINED = 1;//已加入
	}
	
	interface HouseTypeConsts {
		Integer BUILDING = 1;//楼栋
		Integer UNIT = 2;//单元
		Integer FLOOR = 3;//楼层
		Integer DOOR = 4;//房间
	}
}
