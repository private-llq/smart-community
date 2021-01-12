package com.jsy.community.constant;

public interface Const {
	String version = "1.0";
	
	String group = "dev";
	String group_property = "property"; //物业端
	String group_proprietor = "proprietor"; //业主端
	String group_lease = "lease"; //房屋租售端
	
	interface ThirdPlatformConsts {
		int ALIPAY = 1;//支付宝
		int WECHAT = 2;//微信
		int QQ = 3;//QQ
	}
	
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
