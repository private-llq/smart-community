package com.jsy.community.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.jsy.community.api.AiliAppPayRecordService;
import com.jsy.community.api.AliAppPayService;
import com.jsy.community.api.IShoppingMallService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.AlipayUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


/**
* @Description: 支付宝支付实现类
 * @Author: chq459799974
 * @Date: 2021/1/6
**/
@DubboService(version = Const.version, group = Const.group_payment)
public class AliAppPayServiceImpl implements AliAppPayService {
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private AiliAppPayRecordService ailiAppPayRecordService;
	
	@Autowired
	private AlipayUtils alipayUtils;
	
	@DubboReference(version = Const.version, group = Const.group_payment, check = false)
	private IShoppingMallService shoppingMallService;
	
	//下单
	@Override
	public String getOrderStr(AliAppPayQO aliAppPayQO) {
		AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		request.setNotifyUrl("http://222.178.212.29:9951/api/v1/payment/callBack/pay");
		request.setBizContent("{" +
//		"\"timeout_express\":\"90m\"," +
		"\"total_amount\":"+"\""+ aliAppPayQO.getTotalAmount()+"\""+","+
		"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
//		"\"body\":\"Iphone6 16G\"," +
		"\"subject\":"+"\""+ aliAppPayQO.getSubject()+"\""+","+
		"\"out_trade_no\":"+"\""+ aliAppPayQO.getOutTradeNo()+"\""+","+
//		"\"time_expire\":\"5m\"," +
		"  }");
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
