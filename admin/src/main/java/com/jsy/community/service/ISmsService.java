package com.jsy.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.SmsEntity;

/**
 * @Description: 短信配置
 * @author: DKS
 * @since: 2021/12/6 11:14
 */
public interface ISmsService extends IService<SmsEntity> {
	
	/**
	 * @Description: 新增或修改短信配置
	 * @author: DKS
	 * @since: 2021/12/6 11:21
	 * @Param: [smsEntity]
	 * @return: boolean
	 */
	boolean addSmsSetting(SmsEntity smsEntity);
	
	/**
	 * @Description: 查询短信配置
	 * @author: DKS
	 * @since: 2021/12/6 11:35
	 * @Param: []
	 * @return: com.jsy.community.entity.SmsEntity
	 */
	SmsEntity querySmsSetting();
}
