package com.jsy.community.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.AiliAppPayRecordService;
import com.jsy.community.api.AliAppPayService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "支付宝支付控制器")
@RestController
@ApiJSYController
@Login
@RequestMapping("alipay")
public class AliAppPayController {
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private AliAppPayService aliAppPayService;
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	/*下单*/
	@PostMapping("getOrderStr")
	public CommonResult getOrderStr(@RequestBody AliAppPayQO aliAppPayQO, HttpServletRequest req){
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
		String orderNo = String.valueOf(SnowFlake.nextId());
		aliAppPayQO.setOutTradeNo(orderNo);
		aliAppPayQO.setSubject(PaymentEnum.TradeNameEnum.RENT_PAYMENT.getName());
		//TODO 测试金额 0.01
		aliAppPayQO.setTotalAmount(new BigDecimal("0.01"));
		String orderStr = aliAppPayService.getOrderStr(aliAppPayQO);
		boolean createResult = false;
		if(!StringUtils.isEmpty(orderStr)){
			AiliAppPayRecordEntity ailiAppPayRecordEntity = new AiliAppPayRecordEntity();
			ailiAppPayRecordEntity.setOrderNo(orderNo);
			String uid = UserUtils.getUserId();
			ailiAppPayRecordEntity.setUserid(uid);
			ailiAppPayRecordEntity.setTradeAmount(aliAppPayQO.getTotalAmount());
			ailiAppPayRecordEntity.setTradeName(PaymentEnum.TradeNameEnum.RENT_PAYMENT.getIndex());
			ailiAppPayRecordEntity.setTradeType(PaymentEnum.TradeTypeEnum.PAYMENT.getIndex());
			ailiAppPayRecordEntity.setTradeStatus(PaymentEnum.TradeStatusEnum.ORDER_PLACED.getIndex());
			ailiAppPayRecordEntity.setSysType(Integer.valueOf(sysType));
			createResult = ailiAppPayRecordService.createAliAppPayRecord(ailiAppPayRecordEntity);
		}
		return createResult ? CommonResult.ok(orderStr) : CommonResult.error(JSYError.INTERNAL.getCode(),"下单失败");
	}
}
