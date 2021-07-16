package com.jsy.community.api;

import com.jsy.community.entity.lease.AiliAppPayRecordEntity;

/**
 * @author chq459799974
 * @description 支付宝支付记录
 * @since 2021-01-06 14:09
 **/
public interface AiliAppPayRecordService {
	boolean createAliAppPayRecord(AiliAppPayRecordEntity ailiAppPayRecordEntity);//订单创建
	void completeAliAppPayRecord(String outTradeNo);//订单完成
	AiliAppPayRecordEntity getAliAppPayByOutTradeNo(String outTradeNo);//订单查询
	AiliAppPayRecordEntity queryPropertyFeeByOutTradeNo(String outTradeNo);//订单查询(查物业费缴费记录)
}
