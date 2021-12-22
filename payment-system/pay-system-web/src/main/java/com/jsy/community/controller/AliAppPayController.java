package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.untils.wechat.OrderNoUtil;
import com.jsy.community.utils.AlipayUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.BaseTrade;
import com.zhsj.base.api.entity.CreateTradeEntity;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBasePayRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.basecommon.utils.MD5Util;
import com.zhsj.baseweb.annotation.LoginIgnore;
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
// @ApiJSYController
@Slf4j
@RequestMapping("alipay")
public class AliAppPayController {
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AliAppPayService aliAppPayService;

	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private IWeChatService weChatService;
	
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

	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private AssetLeaseRecordService assetLeaseRecordService;

	/**
	 * 收款方为公司时的收款方id
	 */
	@Value("${companyReceiveUid}")
	private Long companyReceiveUid;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Value("${pay.order.timeout}")
	private int payOrderTimeout;

	@DubboReference(version = com.zhsj.base.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.base.api.constant.RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBasePayRpcService basePayRpcService;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;

	/**
	 * 涉及到用户余额的,需要调用基础模块
	 * 不涉及的则走原有的正常支付流程
	 */
	@ApiOperation("下单")
	@PostMapping("order")
	// @Permit("community:payment:alipay:order")
	public CommonResult getOrderStr(@RequestBody AliAppPayQO aliAppPayQO, HttpServletRequest req){
		log.info("创建订单参数:{}", JSON.toJSONString(aliAppPayQO));
		ValidatorUtils.validateEntity(aliAppPayQO,AliAppPayQO.addOrderGroup.class);
		String orderNo = OrderNoUtil.getOrder();
		// 租房和商家订单是涉及到房东或者商家余额的
		if (aliAppPayQO.getTradeFrom() == BusinessEnum.TradeFromEnum.HOUSING_RENTAL.getCode()
				|| aliAppPayQO.getTradeFrom() == BusinessEnum.TradeFromEnum.SHOPPING_MALL.getCode()){
			ValidatorUtils.validateEntity(aliAppPayQO,AliAppPayQO.BalanceInvolvedGroup.class);
			aliAppPayQO.setCommunityId(1L);
			// 调用基础模块
			CreateTradeEntity tradeEntity = new CreateTradeEntity();
			// 服务调用方的订单号(商城的订单号/物业缴费的订单号等)
			tradeEntity.setBusOrderNo(aliAppPayQO.getServiceOrderNo());
			// 付款方的id
			tradeEntity.setSendUid(UserUtils.getEHomeUserId());
			// 收款方的id
			if (aliAppPayQO.getTradeFrom() == BusinessEnum.TradeFromEnum.HOUSING_RENTAL.getCode()) {
				AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordService.contractDetail(aliAppPayQO.getServiceOrderNo());
				UserDetail userDetail = baseUserInfoRpcService.getUserDetail(assetLeaseRecordEntity.getHomeOwnerUid());
				aliAppPayQO.setReceiveUid(userDetail.getId());
			}
			tradeEntity.setReceiveUid(aliAppPayQO.getReceiveUid());
			tradeEntity.setCno("RMB");
			tradeEntity.setAmount(aliAppPayQO.getTotalAmount());
			tradeEntity.setRemark(UserUtils.getUserInfo().getNickname() + "的" + BusinessEnum.TradeFromEnum.tradeMap.get(aliAppPayQO.getTradeFrom()));
			tradeEntity.setType(BusinessEnum.BaseOrderExpensesTypeEnum.getExpenses(aliAppPayQO.getTradeFrom()));
			tradeEntity.setTitle(BusinessEnum.TradeFromEnum.tradeMap.get(aliAppPayQO.getTradeFrom()));
			tradeEntity.setSource(BusinessEnum.BaseOrderSourceEnum.getSourceByCode(aliAppPayQO.getTradeFrom()));
			//签名
			String string = JSON.toJSONString(tradeEntity);
			Map map = JSON.parseObject(string, Map.class);
			map.remove("sign");
			map.put("communicationSecret", BusinessEnum.BaseOrderSourceEnum.getSecretByCode(aliAppPayQO.getTradeFrom()));
			String sign = MD5Util.signStr(map);
			tradeEntity.setSign(MD5Util.getMd5Str(sign));
			//创建交易，成功则返回trade，否则抛出异常
			BaseTrade trade = basePayRpcService.createTrade(tradeEntity);
			orderNo = trade.getSysOrderNo();
		}
		CommunityEntity communityEntity = communityService.getCommunityNameById(aliAppPayQO.getCommunityId());
		String sysType = req.getHeader("sysType");
//		if(!NumberUtil.isInteger(sysType) || (CommonConsts.SYS_ANDROID != Integer.parseInt(sysType)
//				&& CommonConsts.SYS_IOS != Integer.parseInt(sysType))){
//			baseVO.setCode("-1");
//			baseVO.setMsg("请传入正确的系统类型");
//			return baseVO;
//		}
		//TODO 系统类型暂时写死
		sysType = "1";
		aliAppPayQO.setOutTradeNo(orderNo); //本地订单号
		aliAppPayQO.setSubject(PaymentEnum.TradeFromEnum.tradeFromMap.get(aliAppPayQO.getTradeFrom())); //交易类型名称

		if(PaymentEnum.TradeFromEnum.TRADE_FROM_MANAGEMENT.getIndex().equals(aliAppPayQO.getTradeFrom())){
			//缴物业费
			//根据本次缴费月份和房间id 查出应缴金额
			if(StringUtils.isEmpty(aliAppPayQO.getIds())){
				throw new JSYException("缺少缴费账单数据id");
			}
			BigDecimal propertyFee = propertyFinanceOrderService.getTotalMoney(aliAppPayQO.getIds());
			log.info("支付宝 - 查询到物业费缴费金额：" + propertyFee);
			aliAppPayQO.setTotalAmount(propertyFee.abs());
			//缓存缴费账单id
			redisTemplate.opsForValue().set("PropertyFee:" + orderNo,aliAppPayQO.getIds(),payOrderTimeout, TimeUnit.MINUTES);
		} else if (aliAppPayQO.getTradeFrom()==8) {
			//停车缴费逻辑
			if ("".equals(aliAppPayQO.getServiceOrderNo())||aliAppPayQO.getServiceOrderNo()==null){
				return CommonResult.error("车位缴费临时订单记录id不能为空！");
			}
		} else {
			if (aliAppPayQO.getTotalAmount() == null) {
				throw new JSYException("缺少交易金额");
			}
			aliAppPayQO.setTotalAmount(aliAppPayQO.getTotalAmount().abs()); //支付金额
		}
//		//商城订单支付，调用商城接口，校验订单
//		if(PaymentEnum.TradeFromEnum.TRADE_FROM_SHOPPING.getIndex().equals(aliAppPayQO.getTradeFrom())){
//			Map<String, Object> validationMap = shoppingMallService.validateShopOrder(aliAppPayQO.getOrderData(),UserUtils.getUserToken());
//			if(0 != (int)validationMap.get("code")){
//				throw new JSYException((int)validationMap.get("code"),String.valueOf(validationMap.get("msg")));
//			}
//			aliAppPayQO.setServiceOrderNo(String.valueOf(aliAppPayQO.getOrderData().get("uuid")));
//		}
		//TODO 测试金额 0.1
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
		log.info("=============================================");
		log.info(orderStr);
		log.info("=============================================");
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
			ailiAppPayRecordEntity.setCompanyId(communityEntity.getPropertyId() == null ? null : String.valueOf(communityEntity.getPropertyId()));
			createResult = ailiAppPayRecordService.createAliAppPayRecord(ailiAppPayRecordEntity);
		}
		Map<String, String> returnMap = new HashMap<>(2);
		if(createResult){
			returnMap.put("orderStr",orderStr);
			returnMap.put("orderNum",orderNo);
		}
		return createResult ? CommonResult.ok(returnMap, "下单成功") : CommonResult.error(JSYError.INTERNAL.getCode(),"下单失败");
	}
	
	@LoginIgnore
	@PostMapping("close")
	// @Permit("community:payment:alipay:close")
	public void test(@RequestParam Long communityId,@RequestParam String orderId){
		CommunityEntity entity = communityService.getCommunityNameById(communityId);
		PayConfigureEntity serviceConfig;
		if (Objects.nonNull(entity)){
			serviceConfig = payConfigureService.getCompanyConfig(entity.getPropertyId());
			ConstClasses.AliPayDataEntity.setConfig(serviceConfig);
		}
		try {
			AlipayUtils.queryOrder(orderId);
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
	@LoginIgnore
	@GetMapping("/v2/checkPayTradeStatus")
	// @Permit("community:payment:alipay:v2:checkPayTradeStatus")
	public CommonResult checkPayTradeStatus(@RequestParam("orderNo") String orderNo, @RequestParam("serviceOrderNo") String serviceOrderNo) {
		Boolean aliStatus = ailiAppPayRecordService.checkPayTradeStatus(orderNo, serviceOrderNo);
		Boolean wechatStatus = weChatService.checkPayStatus(orderNo, serviceOrderNo);
		return aliStatus || wechatStatus ? CommonResult.ok(true,"查询成功") : CommonResult.ok(false,"查询成功");
	}


}
