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
	 *  房屋租售 房屋介绍内容最大字符
	 * @author YuLF
	 * @since  2020/12/10 10:29
	 */
	short HOUSE_INTRODUCE_CHAR_MAX = 1000;

	/**
	 *  房屋租售 房屋详细地址内容最大字符
	 * @author YuLF
	 * @since  2020/12/10 10:29
	 */
	short HOUSE_ADDRESS_CHAR_MAX = 128;

	/**
	 *  房屋租售 房屋标题最大字符
	 * @author YuLF
	 * @since  2020/12/10 10:29
	 */
	short HOUSE_TITLE_CHAR_MAX = 32;

	/**
	 *  房屋租售 选择省市区ID大范围数字
	 * @author YuLF
	 * @since  2020/12/10 10:29
	 */
	int HOUSE_ID_RANGE_MAX = 999999;

	/**
	 *  房屋租售 房屋面积最大平方
	 * @author YuLF
	 * @since  2020/12/10 10:29
	 */
	short HOUSE_SQUARE_METER_MAX = Short.MAX_VALUE;

	/**
	 *  房屋租售 房屋楼层最大字符
	 * @author YuLF
	 * @since  2020/12/10 10:29
	 */
	short HOUSE_FLOOR_CHAR_MAX = 10;
	
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
	
	/**
	 * 证件类型-身份证
	 */
	Integer IDENTIFICATION_TYPE_IDCARD = 1;
	/**
	 * 证件类型-护照
	 */
	Integer IDENTIFICATION_TYPE_PASSPORT = 2;
}
