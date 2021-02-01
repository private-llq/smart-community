package com.jsy.community.api;

import java.util.Map;

/**
 * @author chq459799974
 * @description 支付宝回调
 * @since 2021-01-06 14:33
 **/
public interface AliAppPayCallbackService {
	
	String dealCallBack(Map<String, String> paramsMap);
	
}
