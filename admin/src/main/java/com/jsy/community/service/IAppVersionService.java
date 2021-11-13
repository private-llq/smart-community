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
	 * @author: DKS
	 * @since: 2021/11/13 13:59
	 * @Param: [sysType, sysVersion]
	 * @return: java.util.List<com.jsy.community.entity.AppVersionEntity>
	 */
	List<AppVersionEntity> queryAppVersionList(Integer sysType, String sysVersion);
	
	/**
	 * @Description: 添加APP版本
	 * @author: DKS
	 * @since: 2021/11/13 13:59
	 * @Param: [appVersionEntity]
	 * @return: void
	 */
	void addAppVersion(AppVersionEntity appVersionEntity);
	
	/**
	 * @Description: 查询APP版本详情 1.安卓 2.IOS
	 * @author: DKS
	 * @since: 2021/11/13 13:59
	 * @Param: [sysType, sysVersion]
	 * @return: com.jsy.community.entity.AppVersionEntity
	 */
	AppVersionEntity queryAppVersion(Integer sysType, String sysVersion);
}
