package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.AiliAppPayRecordService;
import com.jsy.community.api.AliAppPayService;
import com.jsy.community.api.IShoppingMallService;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.OrderNoUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "支付宝支付控制器")
@RestController
@ApiJSYController
//@Login
@RequestMapping("alipay")
public class AliAppPayController {
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AliAppPayService aliAppPayService;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private IShoppingMallService shoppingMallService;
	
	@ApiOperation("下单")
	@PostMapping("order")
	@Login
	public CommonResult getOrderStr(@RequestBody AliAppPayQO aliAppPayQO, HttpServletRequest req){
		ValidatorUtils.validateEntity(aliAppPayQO,AliAppPayQO.addOrderGroup.class);
		//商城订单支付，调用商城接口，校验订单
		if(PaymentEnum.TradeFromEnum.TRADE_FROM_SHOPPING.getIndex().equals(aliAppPayQO.getTradeFrom())){
			Map<String, Object> validationMap = shoppingMallService.validateShopOrder(aliAppPayQO.getOrderData(),UserUtils.getUserToken());
			if(0 != (int)validationMap.get("code")){
				throw new JSYException((int)validationMap.get("code"),String.valueOf(validationMap.get("msg")));
			}
			aliAppPayQO.setServiceOrderNo(String.valueOf(aliAppPayQO.getOrderData().get("uuid")));
		}
		String sysType = req.getHeader("sysType");
//		if(!NumberUtil.isInteger(sysType) || (CommonConsts.SYS_ANDROID != Integer.parseInt(sysType) 
//				&& CommonConsts.SYS_IOS != Integer.parseInt(sysType))){
//			baseVO.setCode("-1");
//			baseVO.setMsg("请传入正确的系统类型");
//			return baseVO;
//		}
		//TODO 系统类型暂时写死
		sysType = "1";
		aliAppPayQO.setTotalAmount(aliAppPayQO.getTotalAmount().abs());
		String orderNo = OrderNoUtil.getOrder();
		aliAppPayQO.setOutTradeNo(orderNo);
		aliAppPayQO.setSubject(PaymentEnum.TradeFromEnum.TRADE_FROM_RENT.getName());
		//TODO 测试金额 0.01
		aliAppPayQO.setTotalAmount(new BigDecimal("0.01"));
		String orderStr = null;
		if(aliAppPayQO.getPayType() == 1){
			orderStr = aliAppPayService.getOrderStr(aliAppPayQO);
		}else if(aliAppPayQO.getPayType() == 2){
			orderStr = aliAppPayService.getOrderStr(aliAppPayQO);
		}else{
			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),"支付类型错误");
		}
		boolean createResult = false;
		if(!StringUtils.isEmpty(orderStr)){
			AiliAppPayRecordEntity ailiAppPayRecordEntity = new AiliAppPayRecordEntity();
			ailiAppPayRecordEntity.setServiceOrderNo(aliAppPayQO.getServiceOrderNo());
			ailiAppPayRecordEntity.setOrderNo(orderNo);
			ailiAppPayRecordEntity.setUserid(UserUtils.getUserId());
			ailiAppPayRecordEntity.setTradeAmount(aliAppPayQO.getTotalAmount());
			ailiAppPayRecordEntity.setTradeName(PaymentEnum.TradeFromEnum.TRADE_FROM_RENT.getIndex());
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
	
}
