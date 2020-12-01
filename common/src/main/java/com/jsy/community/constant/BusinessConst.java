package com.jsy.community.constant;

/**
 * @author chq459799974
 * @description 业务相关常量
 * @since 2020-11-28 13:44
 **/
public interface BusinessConst {
	/**
	 * 房间成员查询类型 - 查询成员
	 */
	Integer QUERY_HOUSE_MEMBER = 1;
	/**
	 * 房间成员查询类型 - 查询邀请
	 */
	Integer QUERY_HOUSE_MEMBER_INVITATION = 2;
	
	/**
	 * 车辆类型最小取值
	 */
	Integer CARTYPE_MIN = 1;
	
	/**
	 * 车辆类型最大取值
	 */
	Integer CARTYPE_MAX = BusinessEnum.CarTypeEnum.values().length;
	
}