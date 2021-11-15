package com.jsy.community.service;

import com.jsy.community.entity.sys.SysPlatformSettingEntity;

/**
 * @author DKS
 * @description 平台设置
 * @since 2021-11-13 14:47
 **/
public interface ISysPlatformSettingService {
	
	/**
	 * @Description: 新增平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:19
	 * @Param: [sysPlatformSettingEntity]
	 * @return: boolean
	 */
	boolean editPlatform(SysPlatformSettingEntity sysPlatformSettingEntity);
	
	/**
	 * @Description: 查询平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:33
	 * @Param: []
	 * @return: com.jsy.community.entity.sys.SysPlatformSettingEntity
	 */
	SysPlatformSettingEntity selectPlatform();
}
