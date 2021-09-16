package com.jsy.community.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.CarOrderRecordEntity;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.untils.OrderNoUtil;
import com.jsy.community.utils.AlipayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * @author chq459799974
 * @description 支付宝回调
 * @since 2021-01-06 14:33
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_lease)
public class AliAppPayCallbackServiceImpl implements AliAppPayCallbackService {
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private IShoppingMallService shoppingMallService;

	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private HousingRentalOrderService housingRentalOrderService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceOrderService propertyFinanceOrderService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceReceiptService propertyFinanceReceiptService;

	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private AssetLeaseRecordService assetLeaseRecordService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPayConfigureService payConfigureService;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICarService carService;

	@Autowired
	private StringRedisTemplate redisTemplate;

	/**
	* @Description: 回调验签/订单处理
	 * @Param: [paramsMap]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	@Override
	public String dealCallBack(Map<String, String> paramsMap){
		CommunityEntity entity = communityService.getCommunityNameById(Long.parseLong(paramsMap.get("passback_params")));
//		CommunityEntity entity = communityService.getCommunityNameById(1L);
		PayConfigureEntity serviceConfig;
		if (Objects.nonNull(entity)){
			serviceConfig = payConfigureService.getCompanyConfig(entity.getPropertyId());
			ConstClasses.AliPayDataEntity.setConfig(serviceConfig);
		}
		boolean signVerified = false;
		//证书验签
		try {
			signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayUtils.getAlipayPublicKey(ConstClasses.AliPayDataEntity.alipayPublicCertPath), "utf-8", "RSA2");
		} catch (AlipayApiException e1) {
			e1.printStackTrace();
			throw new PaymentException("验签出错");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (signVerified){
			log.info("支付宝系统订单：" + paramsMap.get("out_trade_no") + "验签成功");
			//按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
			String outTradeNo = paramsMap.get("out_trade_no");//系统订单号
			String tradeNo = paramsMap.get("trade_no");//支付宝渠道单号
			String totalAmount = paramsMap.get("total_amount");//交易金额
			String receiptAmount = paramsMap.get("receipt_amount");//实收金额
			String sellerId = paramsMap.get("seller_id");//商家支付宝id
			String sellerEmail = paramsMap.get("seller_email");//商家支付宝邮箱账号
			String appId = paramsMap.get("app_id");//appid
			log.info("系统订单号：" + outTradeNo);
			log.info("支付宝渠道单号：" + tradeNo);
			log.info("交易金额：" + totalAmount);
			log.info("实收金额：" + receiptAmount);
			log.info("商家支付宝id：" + sellerId);
			log.info("商家支付宝邮箱账号：" + sellerEmail);
			log.info("appid：" + appId);
			AiliAppPayRecordEntity order = ailiAppPayRecordService.getAliAppPayByOutTradeNo(outTradeNo);
			if(order != null){ // 订单号正确
				log.info("订单号验证通过");
				log.info(totalAmount + ":" + order.getTradeAmount());
				if(new BigDecimal(totalAmount).compareTo(order.getTradeAmount()) == 0){
					log.info("订单金额验证通过");
					if(ConstClasses.AliPayDataEntity.sellerId.equals(sellerId) || ConstClasses.AliPayDataEntity.sellerEmail.equals(sellerEmail)){ // 商家账号正确
						log.info("商家验证通过");
						if(ConstClasses.AliPayDataEntity.appid.equals(appId)){ // appid正确
							log.info("appid验证通过");
							//设置渠道单号
							order.setTradeNo(tradeNo);
							//处理订单
							dealOrder(order);
						}
					}
				}
			}else{
				log.error("支付宝系统订单：" + paramsMap.get("out_trade_no") + "不存在！");
			}
			return "success";
		} else {
			log.error("支付宝系统订单：" + paramsMap.get("out_trade_no") + "验签失败");
//			return "false";
			return "failure";
		}
	}
	
	/**
	* @Description: 回调订单处理
	 * @Param: [order]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/4/8
	**/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void dealOrder(AiliAppPayRecordEntity order){
		log.info("支付宝回调 - 开始处理订单：" + order.getOrderNo());
		if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex().equals(order.getTradeType())){  // 支出
			log.info("开始处理支出订单");
			//支付订单修改状态完成
			ailiAppPayRecordService.completeAliAppPayRecord(order.getOrderNo());
			log.info("支付宝回调 - 本地订单状态修改完成，订单号：" + order.getOrderNo());
			if(PaymentEnum.TradeFromEnum.TRADE_FROM_SHOPPING.getIndex().equals(order.getTradeName())){
				log.info("开始修改商城订单状态，订单号：" + order.getOrderNo());
				Map<String, Object> shopOrderDealMap = shoppingMallService.completeShopOrder(order.getServiceOrderNo());
				if(0 != (int)shopOrderDealMap.get("code")){
					throw new PaymentException((int)shopOrderDealMap.get("code"),String.valueOf(shopOrderDealMap.get("msg")));
				}
				log.info("商城订单状态修改完成，订单号：" + order.getOrderNo());
			} else if (PaymentEnum.TradeFromEnum.HOUSE_RENT_PAYMENT.getIndex().equals(order.getTradeName())) {
				// 房屋押金/房租缴费
				log.info("开始修改房屋押金/房租缴费订单状态，订单号：" + order.getOrderNo());
				// 修改签章合同支付状态
				Map<String, Object> map = housingRentalOrderService.completeLeasingOrder(order.getOrderNo(), order.getServiceOrderNo());
				// 修改租房签约支付状态
				assetLeaseRecordService.updateOperationPayStatus(order.getServiceOrderNo());
				if(0 != (int)map.get("code")){
					throw new PaymentException((int)map.get("code"),String.valueOf(map.get("msg")));
				}
				log.info("房屋押金/房租缴费订单状态修改完成，订单号：" + order.getOrderNo());
			} else if (PaymentEnum.TradeFromEnum.TRADE_FROM_MANAGEMENT.getIndex().equals(order.getTradeName())) {
				log.info("开始修改物业费账单状态，订单号：" + order.getOrderNo());
				String ids = redisTemplate.opsForValue().get("PropertyFee:" + order.getOrderNo());
				if (StringUtils.isEmpty(ids)) {
					log.error("回调处理物业费失败，账单ids未找到，已支付订单号：" + order.getOrderNo());
					return;
				}
				//查询一条账单，获取社区id
				PropertyFinanceOrderEntity financeOrderEntity = propertyFinanceOrderService.findOne(Long.valueOf(ids.split(",")[0]));
				//新增收款单
				PropertyFinanceReceiptEntity receiptEntity = new PropertyFinanceReceiptEntity();
				receiptEntity.setCommunityId(financeOrderEntity.getCommunityId());
				receiptEntity.setReceiptNum(OrderNoUtil.getOrder());
				receiptEntity.setTransactionNo(order.getTradeNo());
				receiptEntity.setTransactionType(1);
				receiptEntity.setReceiptMoney(order.getTradeAmount());
				propertyFinanceReceiptService.add(receiptEntity);
				//修改物业费账单
				propertyFinanceOrderService.updateOrderStatusBatch(2, order.getOrderNo(), ids.split(","));
			} else if (PaymentEnum.TradeFromEnum.TRADE_FROM_PARKING_PAYMENT.getIndex().equals(order.getTradeName())){
				log.info("开始修改停车账单状态，订单号：" + order.getOrderNo());
				CarOrderRecordEntity entity = carService.findOne(Long.parseLong(order.getServiceOrderNo()));
				entity.setOrderNum(order.getOrderNo());
				if (entity!=null){
					if (entity.getType()==1){
						carService.bindingMonthCar(entity);
					}else {
						carService.renewMonthCar(entity);
					}
				}
				log.info("处理完成");
			}
		}else if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex().equals(order.getTradeType())){  // 提现
			log.info("开始处理提现订单");
			log.info("提现订单处理完成");
		}
	}
	
}
