package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jsy.community.api.AiliAppPayRecordService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.AiliAppPayRecordDao;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
* @Description: 支付宝订单记录实现类
 * @Author: chq459799974
 * @Date: 2021/1/6
**/
@DubboService(version = Const.version, group = Const.group_payment)
public class AiliAppPayRecordServiceImpl implements AiliAppPayRecordService {
	
	@Resource
	private AiliAppPayRecordDao ailiAppPayRecordDao;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserService userService;
	
	//创建支付宝订单记录
	@Override
	public boolean createAliAppPayRecord(AiliAppPayRecordEntity ailiAppPayRecordEntity){
		return ailiAppPayRecordDao.insert(ailiAppPayRecordEntity) == 1;
	}
	
	//支付完成修改流水状态(支付完成)
	@Override
	public void completeAliAppPayRecord(String outTradeNo){
		AiliAppPayRecordEntity ailiAppPayRecordEntity = new AiliAppPayRecordEntity();
		ailiAppPayRecordEntity.setTradeStatus(PaymentEnum.TradeStatusEnum.ORDER_COMPLETED.getIndex());
		int result = ailiAppPayRecordDao.update(ailiAppPayRecordEntity, new UpdateWrapper<AiliAppPayRecordEntity>().eq("order_no", outTradeNo));
		if(result != 1){
			throw new PaymentException(JSYError.INTERNAL.getCode(),"订单状态修改异常，请联系管理员");
		}
	}
	
	//查询支付宝订单
	@Override
	public AiliAppPayRecordEntity getAliAppPayByOutTradeNo(String outTradeNo){
		return ailiAppPayRecordDao.selectOne(new QueryWrapper<AiliAppPayRecordEntity>()
				.select("order_no","userid","trade_amount","trade_type","sys_type","trade_name","service_order_no")
				.eq("order_no", outTradeNo)
				.eq("trade_status", PaymentEnum.TradeStatusEnum.ORDER_PLACED.getIndex())
				);
	}
	
	//查询物业费-支付宝付款记录单查
	@Override
	public AiliAppPayRecordEntity queryPropertyFeeByOutTradeNo(String outTradeNo){
		AiliAppPayRecordEntity record = ailiAppPayRecordDao.selectOne(new QueryWrapper<AiliAppPayRecordEntity>()
			.select("order_no","userid","trade_amount","update_time")
			.eq("order_no", outTradeNo)
			.eq("trade_status", PaymentEnum.TradeStatusEnum.ORDER_COMPLETED.getIndex())
			.eq("trade_name",PaymentEnum.TradeFromEnum.TRADE_FROM_MANAGEMENT.getIndex())
		);
		//根据uid查询用户真实姓名
		UserEntity user = userService.queryUserDetailByUid(record.getUserid());
		record.setRealName(user == null ? null : user.getRealName());
		return record;
	}
	
}
