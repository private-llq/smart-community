package com.jsy.community.api;

import com.jsy.community.entity.SmsEntity;

/**
 * @Description: 短信业务
 * @author: DKS
 * @since: 2021/12/7 10:53
 */
public interface ISmsService {
	
	
	SmsEntity querySmsSetting();
}
