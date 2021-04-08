package com.jsy.community.api;

import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.qo.lease.AliAppPayQO;

/**
 * @author chq459799974
 * @description 支付宝支付相关接口
 * @since 2021-01-06 14:09
 **/
public interface AliAppPayService {
	
	String getOrderStr(AliAppPayQO aliAppPayQO); //app下单
//	String getOrderStrForH5(AliAppPayQO aliAppPayQO); //H5下单
	
}
