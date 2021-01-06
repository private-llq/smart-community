package com.jsy.community.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.jsy.community.api.AiliAppPayRecordService;
import com.jsy.community.api.AliAppPayCallbackService;
import com.jsy.community.api.AliAppPayService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author chq459799974
 * @description 支付宝回调
 * @since 2021-01-06 14:33
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_lease)
public class AliAppPayCallbackServiceImpl implements AliAppPayCallbackService {
	
	@Value("${alipay.sellerId}")
	private String aliPaySellerId;
	
	@Value("${alipay.sellerEmail}")
	private String aliPaySellerEmail;
	
	@Value("${alipay.appid}")
	private String aliPayAppid;
	
	@Autowired
	private AliAppPayService aliAppPayService;
	
	@Autowired
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	public String dealCallBack(Map<String, String> paramsMap){
		boolean signVerified;
		//证书验签
		try {
			signVerified = AlipaySignature.rsaCertCheckV1(paramsMap, "/mnt/db/smart-community/cert/ali_cert/alipayCertPublicKey_RSA2.crt", "utf-8", "RSA2");
		} catch (AlipayApiException e1) {
			e1.printStackTrace();
			throw new LeaseException("验签出错");
		}
		//支付宝公钥验签
//		try {
//			signVerified = AlipaySignature.rsaCheckV1(paramsMap,
//					"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlpgw35Df0VnymYrolvYDqc+YF7VSSo3k2cscZQEB6OPycsHqtHGjldvJKPhstixuRpLXzOTkwL4AAwe2yI+AMcCZvIQpgn0qR9Z4/Shpramx8dznyxNqIy70qkU5xnKOac9Iye0hBjWRBNpwj7HHmCpYq0if6ZUhcYCe3+pn7O/qERQLe8iJKEYQcVTwlZKW4VCyGAH8hyryz9axE1yJy7yLo8lbTZutqGkApmzWBln2xcknHA9vB6niO2Oul8f6sdCWnVZTBaZIPkXWyCx38+bMVnpQOUI5KqUImsNgVunzvsrdS6br8qawpo5SLrzpLfmmdZOrDchgpbjfZ1E7GwIDAQAB",
//					"utf-8",
//					"RSA2"
//					);
//		} catch (AlipayApiException e) {
//			e.printStackTrace();
//			throw new MyException("验签出错", new Throwable(e));
//		}
		if (signVerified){
			log.info("支付宝系统订单：" + paramsMap.get("out_trade_no") + "验签成功");
			//按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
			String outTradeNo = paramsMap.get("out_trade_no");//系统订单号
			String totalAmount = paramsMap.get("total_amount");//交易金额
			String receiptAmount = paramsMap.get("receipt_amount");//实收金额
			String sellerId = paramsMap.get("seller_id");//商家支付宝id
			String sellerEmail = paramsMap.get("seller_email");//商家支付宝邮箱账号
			String appId = paramsMap.get("app_id");//appid
			log.info("系统订单号：" + outTradeNo);
			log.info("交易金额：" + totalAmount);
			log.info("实收金额：" + receiptAmount);
			log.info("商家支付宝id：" + sellerId);
			log.info("商家支付宝邮箱账号：" + sellerEmail);
			log.info("appid：" + appId);
			AiliAppPayRecordEntity order = ailiAppPayRecordService.getAliAppPayByOutTradeNo(outTradeNo);
			if(order != null){ // 订单号正确
				log.info("订单号验证通过");
				log.info(totalAmount + ":" + order.getTradeAmount());
				log.info("比较大小：" + new BigDecimal(totalAmount).compareTo(order.getTradeAmount()));
//    			if(String.valueOf(Integer.valueOf(totalAmount.substring(0, totalAmount.indexOf("."))))
//    					.equals(String.valueOf(order.getTradeAmount().toBigInteger()))){ // 订单金额正确
				if(new BigDecimal(totalAmount).compareTo(order.getTradeAmount()) == 0){
					log.info("订单金额验证通过");
					log.info(sellerId + "/" + sellerEmail + ":" + aliPaySellerId + "/" + aliPaySellerEmail);
					if(aliPaySellerId.equals(sellerId) || aliPaySellerEmail.equals(sellerEmail)){ // 商家账号正确
						log.info("商家验证通过");
						log.info(appId + ":" + aliPayAppid);
						if(aliPayAppid.equals(appId)){ // appid正确
							log.info("appid验证通过");
							//处理订单
							aliAppPayService.dealOrder(order);
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
}
