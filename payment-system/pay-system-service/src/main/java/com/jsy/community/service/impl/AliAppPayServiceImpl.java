package com.jsy.community.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.jsy.community.api.AliAppPayService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.AlipayUtils;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
* @Description: 支付宝支付实现类
 * @Author: chq459799974
 * @Date: 2021/1/6
**/
@DubboService(version = Const.version, group = Const.group_payment)
public class AliAppPayServiceImpl implements AliAppPayService {
	
	private static final Logger logger = LoggerFactory.getLogger(AliAppPayServiceImpl.class);
	
	@Autowired
	private AiliAppPayRecordServiceImpl ailiAppPayRecordServiceImpl;
	
	@Autowired
	private AlipayUtils alipayUtils;
	
	/*查询*/
	public static void queryPay() throws AlipayApiException{
		AlipayClient sandBoxCli = new DefaultAlipayClient(
				"https://openapi.alipaydev.com/gateway.do",
				"2021000116688386",
				"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXd5TigKI0FlAXUlQu7khEfZP6Z2MbH3jBUEmaLeCSlw3L3QlI1uk1WQs0uPzkTuEN1vO4yeyzhl+hDoMCdOz1Yh83KqwGiPb60aa8gK/lXrp69yULWlsdJp7Cs/z91bFWM+USLVct9MTJsZ3+4fOZkFE71f78PlmmaFVkOAynU0JpADFzB9SHojfefC2u1wXsX6h4Cvyx2gdP2gJ9apKhlGP14/BEZGsEWXGO4nNJ7PGfTdh/F4TL+LLY9ftuZl/EliOzmAiBOdWPmihJon4/u4CmgJDX6+X+BlnS5w/nA6Ba8it507IymS1lzQIrnS4RXNGFZ9p3MwNfmhNsR2rPAgMBAAECggEBAIDnzXbi4KiRoMHoWCrEsg5QDYMbP/YSV7O1v54Ok0ROd/ha+g7akAjN6kKJePhYxSIBINTQTLkQkAxCjG6n0g6X3lcV4ueA4yjdChj9MDV15CDIeudpk4eAmba8dYAVwGHLspVAryQ2XtAEcKE97RJxByF0YxiLe8KqopmzNdmkKhjtJa3eewNNP5QhXk+UDwp6lK/wY/MRpX9L4b1lKYLSQkBz8DOBc4j9QKjx62gWZ4JBOKqeZAip+J8AjTGvEn/meDEOECbdmEAqzNzbpr3w9UN3hnL3w8RM56X85NMMUbDFvn7H03/Bmui93BfKdnbLDWNI5g6dWffdI5WCMvECgYEA2xQsxf0VLqwPPZTNtki9f91YIelTJFB/z2CUPoLgJ9DmSUSFG7ANRkNoKnxHCqEkMyJvuQpW4/1137WmBiProcgd2n1ouQ4lNYP15g8UEt1H4A/j4SM92CTIurLInNRw4dbg2JQRniN/ywMIGgxY5CmhFc1vgFiyYxMgsXw8f2cCgYEAsP5m7kpSBXYLhaFEAcZD1IsDQG5ayBMwVCh0uprZ/gRXE03yyVPglRtqFaltcudkDtbvmMyX1u4gQ+I1db8dszWWGx9TekJzugxVn/p4Xx9P+z7YdL3212rCa6e4NgSztIvYcxMdHf0hHFC1ht51t0DK9QDVP0QcJrd31pvz4FkCgYEAjWiCVfG8AHozBC7OcYCWQLhgsNLoRaJoPTU7uvXDNjAS1OwTdwG2L8pGrFW3jVbiBNePgcL4vwTVVubrGT+KUszW6DbWp8xHnDlnOW42KM8IG8pl7uZOfvuPTWMlNoWxrzSBwYohrQ+A50s3qYiav/tW7LnD5PZbfrPUAHwOmLUCgYB6iOZB+vomBb+cWDV49QZo04UAymXXNlGzMf7+Yc6h6edO2hxH/eXx1PTw4kd/0WqYS329T4efqWR1GtrgFdw+Ac9WH63vaqosF/X/t6w2TrtUugGpcQq/wI2xcoA6Ba9UQ60qngT+igyuScIjCUh+AkGAtX7tOf4zX7l7vqRsoQKBgGXp8MCQS+6Eav8djJWAMwc52TlZoNDvbCsjC8eTs8cdYU5ZztEMjnnbYVduK94JQD5sd7jJaPwRI8IdaM3c5rFTVHosNYX/oEGzjb2IBV/abbKYwVbw/m35D4NXM/9MPiILOCQsvbDgg9frCPaT2nnt3NnmbSrZxtTlRnh2cU+R",
				"json",
				"utf-8",
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlpgw35Df0VnymYrolvYDqc+YF7VSSo3k2cscZQEB6OPycsHqtHGjldvJKPhstixuRpLXzOTkwL4AAwe2yI+AMcCZvIQpgn0qR9Z4/Shpramx8dznyxNqIy70qkU5xnKOac9Iye0hBjWRBNpwj7HHmCpYq0if6ZUhcYCe3+pn7O/qERQLe8iJKEYQcVTwlZKW4VCyGAH8hyryz9axE1yJy7yLo8lbTZutqGkApmzWBln2xcknHA9vB6niO2Oul8f6sdCWnVZTBaZIPkXWyCx38+bMVnpQOUI5KqUImsNgVunzvsrdS6br8qawpo5SLrzpLfmmdZOrDchgpbjfZ1E7GwIDAQAB",
				"RSA2"
				);
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		request.setBizContent("{" +
		"\"out_trade_no\":\"1597126117563\"," +
//		"\"trade_no\":\"2020081122001400660508309243\"," +
//		"\"org_pid\":\"2088101117952222\"," +
		"      \"query_options\":[" +
		"        \"trade_settle_info\"" +
		"      ]" +
		"  }");
		AlipayTradeQueryResponse response = sandBoxCli.execute(request);
		if(response.isSuccess()){
			System.out.println(response.getBody());
		System.out.println("调用成功");
		} else {
		System.out.println("调用失败");
		}
	}
	
	/*转账*/
	public void transferByCert() throws AlipayApiException{
//		AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
		AlipayClient alipayClient = alipayUtils.getDefaultCertClient();
		AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
		request.setBizContent("{" +
				"\"out_biz_no\":\"202008040001\"," +
				"\"trans_amount\":0.01," +
				"\"product_code\":\"TRANS_ACCOUNT_NO_PWD\"," +
				"\"biz_scene\":\"DIRECT_TRANSFER\"," +
				"\"order_title\":\"U币提现\"," +
//				"\"original_order_id\":\"20190620110075000006640000063056\"," +
				"\"payee_info\":{" +
				"\"identity\":\"15178763584\"," +
				"\"identity_type\":\"ALIPAY_LOGON_ID\"," +
				"\"name\":\"张老板\"" +
				"    }," +
//				"\"remark\":\"单笔转账\"," +
//				"\"business_params\":\"{\\\"sub_biz_scene\\\":\\\"REDPACKET\\\"}\"" +
		"}");
		AlipayFundTransUniTransferResponse response = alipayClient.certificateExecute(request);
		System.out.println(response.getBody());
		if(response.isSuccess()){
		System.out.println("调用成功");
		} else {
		System.out.println("调用失败");
		}
	}
	
	//下单
	@Override
	public String getOrderStr(AliAppPayQO aliAppPayQO) {
		AlipayClient alipayClient = null;
//		try {
//			alipayClient = new DefaultAlipayClient(certAlipayRequest);
			alipayClient = alipayUtils.getDefaultCertClient();
//		} catch (AlipayApiException e1) {
//			e1.printStackTrace();
//			throw new PaymentException("支付初始化出错");
//		}
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//		request.setNotifyUrl("http://jsy.free.vipnps.vip/callBack/pay");
		request.setNotifyUrl("http://222.178.212.29:9951/api/v1/payment/callBack/pay");
		request.setBizContent("{" +
//		"\"timeout_express\":\"90m\"," +
		"\"total_amount\":"+"\""+ aliAppPayQO.getTotalAmount()+"\""+","+
		"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
//		"\"body\":\"Iphone6 16G\"," +
		"\"subject\":"+"\""+ aliAppPayQO.getSubject()+"\""+","+
		"\"out_trade_no\":"+"\""+ aliAppPayQO.getOutTradeNo()+"\""+","+
//		"\"time_expire\":\"5m\"," +
//		"\"goods_type\":\"0\"," +
//		"\"promo_params\":\"{\\\"storeIdType\\\":\\\"1\\\"}\"," +
//		"\"passback_params\":\"merchantBizType%3d3C%26merchantBizNo%3d2016010101111\"," +
//		"\"extend_params\":{" +
//		"\"sys_service_provider_id\":\"2088511833207846\"," +
//		"\"hb_fq_num\":\"3\"," +
//		"\"hb_fq_seller_percent\":\"100\"," +
//		"\"industry_reflux_info\":\"{\\\\\\\"scene_code\\\\\\\":\\\\\\\"metro_tradeorder\\\\\\\",\\\\\\\"channel\\\\\\\":\\\\\\\"xxxx\\\\\\\",\\\\\\\"scene_data\\\\\\\":{\\\\\\\"asset_name\\\\\\\":\\\\\\\"ALIPAY\\\\\\\"}}\"," +
//		"\"card_type\":\"S0JP0000\"" +
//		"    }," +
//		"\"merchant_order_no\":\"20161008001\"," +
//		"\"enable_pay_channels\":\"pcredit,moneyFund,debitCardExpress\"," +
//		"\"store_id\":\"NJ_001\"," +
//		"\"specified_channel\":\"pcredit\"," +
//		"\"disable_pay_channels\":\"pcredit,moneyFund,debitCardExpress\"," +
//		"      \"goods_detail\":[{" +
//		"        \"goods_id\":\"apple-01\"," +
//		"\"alipay_goods_id\":\"20010001\"," +
//		"\"goods_name\":\"ipad\"," +
//		"\"quantity\":1," +
//		"\"price\":2000," +
//		"\"goods_category\":\"34543238\"," +
//		"\"categories_tree\":\"124868003|126232002|126252004\"," +
//		"\"body\":\"特价手机\"," +
//		"\"show_url\":\"http://www.alipay.com/xxx.jpg\"" +
//		"        }]," +
//		"\"ext_user_info\":{" +
//		"\"name\":\"李明\"," +
//		"\"mobile\":\"16587658765\"," +
//		"\"cert_type\":\"IDENTITY_CARD\"," +
//		"\"cert_no\":\"362334768769238881\"," +
//		"\"min_age\":\"18\"," +
//		"\"fix_buyer\":\"F\"," +
//		"\"need_check_info\":\"F\"" +
//		"    }," +
//		"\"business_params\":\"{\\\"data\\\":\\\"123\\\"}\"," +
//		"\"agreement_sign_params\":{" +
//		"\"personal_product_code\":\"CYCLE_PAY_AUTH_P\"," +
//		"\"sign_scene\":\"INDUSTRY|DIGITAL_MEDIA\"," +
//		"\"external_agreement_no\":\"test20190701\"," +
//		"\"external_logon_id\":\"13852852877\"," +
//		"\"access_params\":{" +
//		"\"channel\":\"ALIPAYAPP\"" +
//		"      }," +
//		"\"sub_merchant\":{" +
//		"\"sub_merchant_id\":\"2088123412341234\"," +
//		"\"sub_merchant_name\":\"滴滴出行\"," +
//		"\"sub_merchant_service_name\":\"滴滴出行免密支付\"," +
//		"\"sub_merchant_service_description\":\"免密付车费，单次最高500\"" +
//		"      }," +
//		"\"period_rule_params\":{" +
//		"\"period_type\":\"DAY\"," +
//		"\"period\":3," +
//		"\"execute_time\":\"2019-01-23\"," +
//		"\"single_amount\":10.99," +
//		"\"total_amount\":600" +
//		"\"total_payments\":12" +
//		"      }," +
//		"\"allow_huazhi_degrade\":false," +
//		"\"sign_notify_url\":\"http://www.merchant.com/receiveSignNotify\"" +
//		"    }" +
		"  }");
		AlipayTradeAppPayResponse response = null;
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
	public String getOrderStrForH5(AliAppPayQO aliAppPayQO) {
		AlipayClient alipayClient = null;
//		try {
//			alipayClient = new DefaultAlipayClient(certAlipayRequest);
			alipayClient = alipayUtils.getDefaultCertClient();
//		} catch (AlipayApiException e1) {
//			e1.printStackTrace();
//			throw new PaymentException("支付初始化出错");
//		}
		AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
		request.setNotifyUrl("http://xxxxxxxxxxxxx:8001/callBack/pay");
		request.setBizContent("{" +
			"\"total_amount\":"+"\""+ aliAppPayQO.getTotalAmount()+"\""+","+
			"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
			"\"subject\":"+"\""+ aliAppPayQO.getSubject()+"\""+","+
			"\"out_trade_no\":"+"\""+ aliAppPayQO.getOutTradeNo()+"\""+","+
			"  }");
		AlipayTradeWapPayResponse response = null;
		try {
			response = alipayClient.pageExecute(request);
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
	
	//处理回调订单
	public void dealOrder(AiliAppPayRecordEntity order){
		logger.error("开始处理订单：" + order.getOrderNo());
		if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_EXPEND.getIndex().equals(order.getTradeType())){  // 充值
			logger.error("开始处理充值订单");
			//支付订单修改状态完成
			boolean completeAliAppPayRecord = ailiAppPayRecordServiceImpl.completeAliAppPayRecord(order.getOrderNo());
			logger.error("订单状态修改完成" + completeAliAppPayRecord + "订单号：" + order.getOrderNo());
		}else if(PaymentEnum.TradeTypeEnum.TRADE_TYPE_INCOME.getIndex().equals(order.getTradeType())){  // 提现
			logger.error("开始处理提现订单");
			logger.error("提现订单处理完成");
		}
	}
	
}
