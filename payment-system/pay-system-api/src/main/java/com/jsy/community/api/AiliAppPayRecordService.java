package com.jsy.community.api;

import com.jsy.community.entity.lease.AiliAppPayRecordEntity;

/**
 * @author chq459799974
 * @description 支付宝支付记录
 * @since 2021-01-06 14:09
 **/
public interface AiliAppPayRecordService {
	boolean createAliAppPayRecord(AiliAppPayRecordEntity ailiAppPayRecordEntity);
	AiliAppPayRecordEntity getAliAppPayByOutTradeNo(String outTradeNo);
	boolean completeAliAppPayRecord(String outTradeNo);
}
