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
	 * 小区门禁-无
	 */
	String ACCESS_COMMUNITY_NONE = "0";
	/**
	 * 小区门禁-二维码
	 */
	String ACCESS_COMMUNITY_QR_CODE = "1";
	/**
	 * 小区门禁-人脸识别
	 */
	String ACCESS_COMMUNITY_FACE = "2";
	/**
	 * 楼栋门禁-无
	 */
	String ACCESS_BUILDING_NONE = "0";
	/**
	 * 楼栋门禁-二维码
	 */
	String ACCESS_BUILDING_QR_CODE = "1";
	/**
	 * 楼栋门禁-可视对讲
	 */
	String ACCESS_BUILDING_COMMUNICATION = "2";
	
}
