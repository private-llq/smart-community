package com.jsy.community.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.AlipayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;


/**
* @Description: 支付宝支付实现类
 * @Author: chq459799974
 * @Date: 2021/1/6
**/
@DubboService(version = Const.version, group = Const.group_payment)
public class AliAppPayServiceImpl implements AliAppPayService {
	
	@Value("${notifyUrl}")
	private String notifyUrl;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	@Autowired
	private AlipayUtils alipayUtils;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private IShoppingMallService shoppingMallService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPayConfigureService payConfigureService;
	
	//下单
	@Override
	public String getOrderStr(AliAppPayQO aliAppPayQO) {
		CommunityEntity entity = communityService.getCommunityNameById(aliAppPayQO.getCommunityId());
		PayConfigureEntity serviceConfig;
		if (Objects.nonNull(entity)){
			serviceConfig = payConfigureService.getCompanyConfig(entity.getPropertyId());
			ConstClasses.AliPayDataEntity.setConfig(serviceConfig);
		}
		AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		request.setNotifyUrl(notifyUrl);
		request.setBizContent("{" +
//		"\"timeout_express\":\"90m\"," +
		"\"total_amount\":"+"\""+ aliAppPayQO.getTotalAmount()+"\""+","+
		"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
//		"\"body\":\"Iphone6 16G\"," +
		"\"subject\":"+"\""+ aliAppPayQO.getSubject()+"\""+","+
		"\"out_trade_no\":"+"\""+ aliAppPayQO.getOutTradeNo()+"\""+","+
		"\"passback_params\":"+"\""+ aliAppPayQO.getCommunityId()+"\""+","+
//		"\"time_expire\":\"5m\"," +
		"  }");
		System.out.println("=============================================");
		System.out.println(request.getBizContent());
		System.out.println("=============================================");
		AlipayTradeAppPayResponse response;
		try {
			response = alipayClient.sdkExecute(request);
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return null;
		}
		if(response == null){
			System.out.println("调用失败");
			return null;
		}
		String body = response.getBody();
		if(response.isSuccess()){
			System.out.println("调用成功");
		//return body.substring(body.indexOf('&')+1);
			return body;
		} else {
			System.out.println("调用失败");
		}
		return null;
	}
	
	/*转账*/
	@Override
	public void transferByCert() {
		AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
		AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
		request.setBizContent("{" +
			"\"out_biz_no\":\"202008040001\"," +
			"\"trans_amount\":0.01," +
			"\"product_code\":\"TRANS_ACCOUNT_NO_PWD\"," +
			"\"biz_scene\":\"DIRECT_TRANSFER\"," +
			"\"order_title\":\"转账测试\"," +
//				"\"original_order_id\":\"20190620110075000006640000063056\"," +
			"\"payee_info\":{" +
			"\"identity\":\"15178763584\"," +
			"\"identity_type\":\"ALIPAY_LOGON_ID\"," +
//			"\"name\":\"name\"" +
			"    }," +
//				"\"remark\":\"单笔转账\"," +
//				"\"business_params\":\"{\\\"sub_biz_scene\\\":\\\"REDPACKET\\\"}\"" +
			"}");
		AlipayFundTransUniTransferResponse response = null;
		try {
			response = alipayClient.certificateExecute(request);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		if(response != null && response.isSuccess()){
			System.out.println(response.getBody());
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
	}
	
	//下单(H5)
//	public String getOrderStrForH5(AliAppPayQO aliAppPayQO) {
//		AlipayClient alipayClient = null;
////		try {
////			alipayClient = new DefaultAlipayClient(certAlipayRequest);
//		alipayClient = alipayUtils.getDefaultCertClient();
////		} catch (AlipayApiException e1) {
////			e1.printStackTrace();
////			throw new PaymentException("支付初始化出错");
////		}
//		AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
//		request.setNotifyUrl("http://xxxxxxxxxxxxx:8001/callBack/pay");
//		request.setBizContent("{" +
//			"\"total_amount\":"+"\""+ aliAppPayQO.getTotalAmount()+"\""+","+
//			"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
//			"\"subject\":"+"\""+ aliAppPayQO.getSubject()+"\""+","+
//			"\"out_trade_no\":"+"\""+ aliAppPayQO.getOutTradeNo()+"\""+","+
//			"  }");
//		AlipayTradeWapPayResponse response = null;
//		try {
//			response = alipayClient.pageExecute(request);
//		} catch (AlipayApiException e) {
//			e.printStackTrace();
//			return null;
//		}
//		if(response == null){
//			System.out.println("调用失败");
//			return null;
//		}
//		String body = response.getBody();
//		if(response.isSuccess()){
//			System.out.println("调用成功");
//			//return body.substring(body.indexOf('&')+1);
//			return body;
//		} else {
//			System.out.println("调用失败");
//		}
//		return null;
//	}

}
