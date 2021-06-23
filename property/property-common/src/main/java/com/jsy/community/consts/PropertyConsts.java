package com.jsy.community.consts;

/**
 * @author chq459799974
 * @description 物业端常量
 * @since 2021-04-01 13:44
 **/
public interface PropertyConsts {
	//======= 账户类型
	Integer ACCOUNT_TYPE_SUPER_ADMIN = 1; //超级管理员
	Integer ACCOUNT_TYPE_NORMAL_ADMIN = 2; //普通用户
	
	//======= 登录账户是否设置密码
	Integer ACCOUNT_PASS_HAD_NOT = 0; //密码未设置
	Integer ACCOUNT_PASS_HAD = 1; //密码已设置
	
	//======= 轮播图发布类型
	Integer BANNER_PUB_TYPE_DRAFT = 0; //草稿
	Integer BANNER_PUB_TYPE_PUBLISH = 1; //已发布
	
	//======= 轮播图状态
	Integer BANNER_STATUS_CANCEL = 0; //已撤销
	Integer BANNER_STATUS_PUBLISH = 1; //发布中
	
	//======= 头像文件夹
	String BUCKET_NAME_AVATAR = "admin-avatar";
	
	//======= 设备数据同步状态
	Integer FACILITY_SYNC_HAVA_NOT = 0; //未同步
	Integer FACILITY_SYNC_DONE = 1; //已同步
	Integer FACILITY_SYNC_DOING = 2; //同步中
}
