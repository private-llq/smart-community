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
}
