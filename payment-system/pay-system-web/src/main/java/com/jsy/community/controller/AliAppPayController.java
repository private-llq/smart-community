package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.AlipayUtils;
import com.jsy.community.utils.OrderNoUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@ApiJSYController
@Slf4j
//@Login
@RequestMapping("alipay")
public class AliAppPayController {
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AliAppPayService aliAppPayService;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private IShoppingMallService shoppingMallService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyFinanceOrderService propertyFinanceOrderService;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;

	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPayConfigureService payConfigureService;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Value("${pay.order.timeout}")
	private int payOrderTimeout;
	
	@ApiOperation("下单")
	@PostMapping("order")
	@Login
	public CommonResult getOrderStr(@RequestBody AliAppPayQO aliAppPayQO, HttpServletRequest req){
		if (aliAppPayQO.getTradeFrom()==9){
			aliAppPayQO.setCommunityId(1L);
		}
		ValidatorUtils.validateEntity(aliAppPayQO,AliAppPayQO.addOrderGroup.class);
		String sysType = req.getHeader("sysType");
//		if(!NumberUtil.isInteger(sysType) || (CommonConsts.SYS_ANDROID != Integer.parseInt(sysType)
//				&& CommonConsts.SYS_IOS != Integer.parseInt(sysType))){
//			baseVO.setCode("-1");
//			baseVO.setMsg("请传入正确的系统类型");
//			return baseVO;
//		}
		//TODO 系统类型暂时写死
		sysType = "1";
		String orderNo = OrderNoUtil.getOrder();
		aliAppPayQO.setOutTradeNo(orderNo); //本地订单号
		aliAppPayQO.setSubject(PaymentEnum.TradeFromEnum.tradeFromMap.get(aliAppPayQO.getTradeFrom())); //交易类型名称
		
		//缴物业费
		if(PaymentEnum.TradeFromEnum.TRADE_FROM_MANAGEMENT.getIndex().equals(aliAppPayQO.getTradeFrom())){
			//根据本次缴费月份和房间id 查出应缴金额
			if(StringUtils.isEmpty(aliAppPayQO.getIds())){
				throw new JSYException("缺少缴费账单数据id");
			}
			BigDecimal propertyFee = propertyFinanceOrderService.getTotalMoney(aliAppPayQO.getIds());
			log.info("支付宝 - 查询到物业费缴费金额：" + propertyFee);
			aliAppPayQO.setTotalAmount(propertyFee.abs());
			//缓存缴费账单id
			redisTemplate.opsForValue().set("PropertyFee:" + orderNo,aliAppPayQO.getIds(),payOrderTimeout, TimeUnit.MINUTES);
		}else{
			if(aliAppPayQO.getTotalAmount() == null){
				throw new JSYException("缺少交易金额");
			}
			aliAppPayQO.setTotalAmount(aliAppPayQO.getTotalAmount().abs()); //支付金额
		}
		//商城订单支付，调用商城接口，校验订单
		if(PaymentEnum.TradeFromEnum.TRADE_FROM_SHOPPING.getIndex().equals(aliAppPayQO.getTradeFrom())){
			Map<String, Object> validationMap = shoppingMallService.validateShopOrder(aliAppPayQO.getOrderData(),UserUtils.getUserToken());
			if(0 != (int)validationMap.get("code")){
				throw new JSYException((int)validationMap.get("code"),String.valueOf(validationMap.get("msg")));
			}
			aliAppPayQO.setServiceOrderNo(String.valueOf(aliAppPayQO.getOrderData().get("uuid")));
		}
		//停车缴费逻辑
		if (aliAppPayQO.getTradeFrom()==8){
			if ("".equals(aliAppPayQO.getServiceOrderNo())||aliAppPayQO.getServiceOrderNo()==null){
				return CommonResult.error("车位缴费临时订单记录id不能为空！");
			}
		}
		//TODO 测试金额 0.01
		aliAppPayQO.setTotalAmount(new BigDecimal("0.01"));
		
		String orderStr = null;
//		if(aliAppPayQO.getPayType() == 1){
//			orderStr = aliAppPayService.getOrderStr(aliAppPayQO);
//		}else if(aliAppPayQO.getPayType() == 2){
//			orderStr = aliAppPayService.getOrderStr(aliAppPayQO);
//		}else{
//			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),"支付类型错误");
//		}
		orderStr = aliAppPayService.getOrderStr(aliAppPayQO);
		boolean createResult = false;
		if(!StringUtils.isEmpty(orderStr)){
			AiliAppPayRecordEntity ailiAppPayRecordEntity = new AiliAppPayRecordEntity();
			ailiAppPayRecordEntity.setServiceOrderNo(aliAppPayQO.getServiceOrderNo());
			ailiAppPayRecordEntity.setOrderNo(orderNo);
			ailiAppPayRecordEntity.setUserid(UserUtils.getUserId());
			ailiAppPayRecordEntity.setTradeAmount(aliAppPayQO.getTotalAmount());
			ailiAppPayRecordEntity.setTradeName(aliAppPayQO.getTradeFrom());
			ailiAppPayRecordEntity.setTradeType(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex());
			ailiAppPayRecordEntity.setTradeStatus(PaymentEnum.TradeStatusEnum.ORDER_PLACED.getIndex());
			ailiAppPayRecordEntity.setSysType(Integer.valueOf(sysType));
			createResult = ailiAppPayRecordService.createAliAppPayRecord(ailiAppPayRecordEntity);
		}
		Map<String, String> returnMap = new HashMap<>(2);
		if(createResult){
			returnMap.put("orderStr",orderStr);
			returnMap.put("orderNum",orderNo);
		}
		return createResult ? CommonResult.ok(returnMap, "下单成功") : CommonResult.error(JSYError.INTERNAL.getCode(),"下单失败");
	}
	
	@PostMapping("close")
	public void test(@RequestParam Long communityId,@RequestParam String orderId){
		CommunityEntity entity = communityService.getCommunityNameById(communityId);
		PayConfigureEntity serviceConfig;
		if (Objects.nonNull(entity)){
			serviceConfig = payConfigureService.getCompanyConfig(entity.getPropertyId());
			ConstClasses.AliPayDataEntity.setConfig(serviceConfig);
		}
		try {
			AlipayUtils.closeOrder(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author: Pipi
	 * @description: 租房订单查询支付订单支付状态
	 * @param orderNo: 支付订单号
     * @param serviceOrderNo: 租房合同号
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/17 10:17
	 **/
	@GetMapping("/v2/checkPayTradeStatus")
	public CommonResult checkPayTradeStatus(@RequestParam("orderNo") String orderNo, @RequestParam("serviceOrderNo") String serviceOrderNo) {
		return CommonResult.ok(ailiAppPayRecordService.checkPayTradeStatus(orderNo, serviceOrderNo),"查询成功");
	}
}
