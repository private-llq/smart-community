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
	
	//========= 小区门禁方式 ============
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
	
	//========= 楼栋门禁方式 ============
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
	
	//========= 证件类型 ================
	/**
	 * 证件类型-身份证
	 */
	Integer IDENTIFICATION_TYPE_IDCARD = 1;
	/**
	 * 证件类型-护照
	 */
	Integer IDENTIFICATION_TYPE_PASSPORT = 2;
	
	//========= 房间成员类型 ===========
	/**
	 * 房间成员类型-亲属
	 */
	Integer PERSON_TYPE_RELATIVE = 1;
	/**
	 * 房间成员类型-租客
	 */
	Integer PERSON_TYPE_TENANT = 2;

	/**
	 * 租房异步线程池
	 */
	String LEASE_ASYNC_POOL = "leaseAsyncThreadPool";
	
	//========= 交易类型 ==========
	/**
	 * 交易类型-私包
	 */
	Integer BUSINESS_TYPE_PRIVATE_REDBAG = 1;
	/**
	 * 交易类型-群红包
	 */
	Integer BUSINESS_TYPE_GROUP_REDBAG = 2;
	/**
	 * 交易类型-转账
	 */
	Integer BUSINESS_TYPE_TRANSFER = 3;
	
	//========= 红包状态 ==========
	/**
	 * 红包状态-未领取
	 */
	Integer REDBAG_STATUS_UNCLAIMED = 0;
	/**
	 * 红包状态-领取中
	 */
	Integer REDBAG_STATUS_RECEIVING = 1;
	/**
	 * 红包状态-已领完
	 */
	Integer REDBAG_STATUS_FINISHED = 2;
	/**
	 * 红包状态-已退回
	 */
	Integer REDBAG_STATUS_BACK = -1;
	
	//========= 红包来源主体 ==========
	/**
	 * 红包来源-个人
	 */
	Integer REDBAG_FROM_TYPE_PERSON = 1;
	/**
	 * 红包来源-官方
	 */
	Integer REDBAG_FROM_TYPE_OFFICIAL = 2;
	
	//========= 交易行为 ==========
	/**
	 * 交易行为-发红包
	 */
	Integer BEHAVIOR_SEND = 1;
	/**
	 * 交易行为-领红包
	 */
	Integer BEHAVIOR_RECEIVE = 2;
	/**
	 * 交易行为-退红包
	 */
	Integer BEHAVIOR_BACK = 3;
	
	//========= 社区房屋层级模式 ==========
	/**
	 * 社区模式-楼栋单元
	 */
	Integer COMMUNITY_MODE_FLOOR_UNIT = 1;
	/**
	 * 社区模式-单元楼栋
	 */
	Integer COMMUNITY_MODE_UNIT_FLOOR = 2;
	/**
	 * 社区模式-单楼栋
	 */
	Integer COMMUNITY_MODE_FLOOR = 3;
	/**
	 * 社区模式-单单元
	 */
	Integer COMMUNITY_MODE_UNIT = 4;
	
	//========= 楼宇单位 ==========
	/**
	 * 楼栋
	 */
	int BUILDING_TYPE_BUILDING = 1;
	/**
	 * 单元
	 */
	int BUILDING_TYPE_UNIT = 2;
	/**
	 * 楼层
	 */
	int BUILDING_TYPE_FLOOR = 3;
	/**
	 * 房间
	 */
	int BUILDING_TYPE_DOOR = 4;

	//========= 文件上传 文件夹分类名称 ==========
	/**
	 * 用户头像 上传 至 文件服务器 的 bucket Name
	 */
	String AVATAR_BUCKET_NAME = "user-avatar";

	/**
	 * 车辆图片 上传 至 文件服务器 的 bucket Name
	 */
	String CAR_IMAGE_BUCKET_NAME = "user-avatar";
}
