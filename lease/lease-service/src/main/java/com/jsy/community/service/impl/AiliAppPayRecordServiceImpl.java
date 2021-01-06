package com.jsy.community.service.impl;

import com.jsy.community.api.AiliAppPayRecordService;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.mapper.AiliAppPayRecordDao;
import org.apache.dubbo.config.annotation.DubboService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import javax.annotation.Resource;

/**
* @Description: 支付宝订单记录实现类
 * @Author: chq459799974
 * @Date: 2021/1/6
**/
@DubboService(version = Const.version, group = Const.group_lease)
public class AiliAppPayRecordServiceImpl implements AiliAppPayRecordService {
	
	@Resource
	private AiliAppPayRecordDao ailiAppPayRecordDao;
	
	//创建支付宝订单记录
	public boolean createAliAppPayRecord(AiliAppPayRecordEntity ailiAppPayRecordEntity){
		return ailiAppPayRecordDao.insert(ailiAppPayRecordEntity) == 1;
	}
	
	//查询支付宝订单
	public AiliAppPayRecordEntity getAliAppPayByOutTradeNo(String outTradeNo){
		return ailiAppPayRecordDao.selectOne(new QueryWrapper<AiliAppPayRecordEntity>()
				.select("order_no","userid","trade_amount,trade_type,sys_type")
				.eq("order_no", outTradeNo)
				.eq("trade_status", 1)
				);
	}
	
	//支付完成修改流水状态(支付完成)
	public boolean completeAliAppPayRecord(String outTradeNo){
		AiliAppPayRecordEntity ailiAppPayRecordEntity = new AiliAppPayRecordEntity();
		ailiAppPayRecordEntity.setTradeStatus(PaymentEnum.TradeStatusEnum.ORDER_COMPLETED.getIndex());
		return ailiAppPayRecordDao.update(ailiAppPayRecordEntity, new UpdateWrapper<AiliAppPayRecordEntity>().eq("order_no", outTradeNo)) == 1;
	}
	
}
