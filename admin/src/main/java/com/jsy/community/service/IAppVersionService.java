package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.AppVersionEntity;

import java.util.List;

/**
 * @author DKS
 * @description APP版本Service
 * @since 2021-11-11 15:24
 **/
public interface IAppVersionService extends IService<AppVersionEntity> {
	
	/**
	* @Description: 查询APP版本列表 1.安卓 2.IOS
	 * @Param: [sysType,sysVersion]
	 * @Return: java.util.List<com.jsy.community.entity.AppVersionEntity>
	 * @Author: chq459799974
	 * @Date: 2021/5/31
	**/
	List<AppVersionEntity> queryAppVersionList(Integer sysType, String sysVersion);
	
	/**
	* @Description: 添加APP版本
	 * @Param: [appVersionEntity]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/12
	**/
	void addAppVersion(AppVersionEntity appVersionEntity);
	
	/**
	 * @Description: 查询APP版本详情 1.安卓 2.IOS
	 * @Param: [sysType]
	 * @Return: com.jsy.community.entity.AppVersionEntity
	 * @Author: DKS
	 * @Date: 2021/9/22
	 **/
	AppVersionEntity queryAppVersion(Integer sysType, String sysVersion);
}
