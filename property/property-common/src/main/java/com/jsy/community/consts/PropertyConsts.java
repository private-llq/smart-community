package com.jsy.community.consts;

/**
 * @author chq459799974
 * @description 物业端常量
 * @since 2021-04-01 13:44
 **/
public interface PropertyConsts {
	/**
	 * 超级管理员
	 */
	int SUPER_ADMIN = 1;
	
	//======= 轮播图发布类型
	Integer BANNER_PUB_TYPE_DRAFT = 0; //草稿
	Integer BANNER_PUB_TYPE_PUBLISH = 1; //已发布
	
	//======= 轮播图状态
	Integer BANNER_STATUS_CANCEL = 0; //已撤销
	Integer BANNER_STATUS_PUBLISH = 1; //发布中
	
	//======= 头像文件夹
	String BUCKET_NAME_AVATAR = "admin-avatar";
}
