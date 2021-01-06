package com.jsy.community.service.impl;

import com.jsy.community.api.AliAppPayService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.qo.lease.AliAppPayQO;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

import java.math.BigDecimal;

/**
* @Description: 支付宝支付实现类
 * @Author: chq459799974
 * @Date: 2021/1/6
**/
@DubboService(version = Const.version, group = Const.group_lease)
public class AliAppPayServiceImpl implements AliAppPayService {
	
	private static final Logger logger = LoggerFactory.getLogger(AliAppPayServiceImpl.class);
	
	@Autowired
	private AiliAppPayRecordServiceImpl ailiAppPayRecordServiceImpl;
	
	//沙箱测试 初始化
	public static final AlipayClient sandBoxCli = new DefaultAlipayClient(
			"https://openapi.alipaydev.com/gateway.do",
			"2021000116687297",
			"MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC6E0Dp1sP8XPGwOGP+HEFS5OPfmoFaJA0qloHShVCSBjt8+53+soc3v/RemCMdzWUsugpH/Lzyxo2qV3GpDMqVi5/UJXbI573RZX/SydAvLNhGJXwlqKnM9C4t1NuhQkbzYIzySYPweGK/e6m+s89w/+HXmNeSxRLOW9KJvGGHxJuzYRrshfi81YY1VNpF2CGAjoITXHp8UziEQKotv+cnJt/ZFA1dITt3kuWQzeL1nPFxJfmqXdxyoScr6HFNpnWmGSo0s8mqD4XvF1XGg9cSKF5ekFnjaPUZKuIyOa7ko15JsbdOFeTKwjdqTGkek1xb2fX91EUDlv9ux2RxH5izAgMBAAECggEBALG7yEpTuLop8TV4d5J2XLMYLM0dYKbAPs9KGdEDRBVm3JWiJaTt1BidQobEXO6PjS9uuHlQYQ6oNxLZGZj2vd6gEldqPY7jRD1p7PFjkXYuKJrAHVg/RYmeRynB4tOiYfEAbUDUlbEw3Gm4bkfYqrFtyMPNm0P29+KidHTZunMZ5ybmeqvAxIxHxFJEOeJ4LdQan/+HoH2uhevEqCdLof5jw7ZYi1qUSTHwpMSJ84YMT/sjo60buxWZGUphyojEK98SjwopWOaQK6sLVvrL4xrbARh5cnQLp1u4SK6OJy7edGuTMEAZI1YlTxeSD//ODZzTlR3Mk7sXqrfYJxdT2pECgYEA7/84b174WlMK2tfDJ+xQ4EkKupcxTPvYbJoVCPKW1X4HzkaPXiB6HNKI8VxKAMCUujxF1OQODawxHE5ysKGG8xL8w1wso1O8Li2qPazkHArun3zPGv2zJ2LRTTCoHZ9/a2VivjlFeaWiG7Tt7krq6llfCY8dz/FxFCRLBhmZApkCgYEAxnuU8z3G77JG9Khkk4x2jD8EZfxxMLX8pxubNnZIlOfTtenWM8LwFfx6RQ7vARp+8D5Jf6lGRB1Nbd7VbnI2ar1rXJ1uyLyRQdjc+JofEg3nP3ZwgrlfAo46pbNBRES0TsiEh7K4pPUahmq34fHTgJGiEqKeQed1xqaddx1AESsCgYEAhS0cFPkfWYgBWwUl326MRplhob8qdWy/YSxzhb6QIrJIBJg1EGd3O608nqq2ygoTvfCaU5OllMK7kdiaXheUOiiMKKBqiMt7fiiK8qSRBFqwpcHtby3fD3kPagUZFS8f1umwzJwdSG64MSXKblQWXarwSO6o/W4ecK3fD+h+dukCgYEAlniSD2XCL22kRU50ETfnl6MenoKm/Rq6k6VmIf/CPsERy1OfRFaA1Oo3d6s+nkOMAG34IoQd9P2R+b15aOXFQ16eyfPATQZHyPezbfTJoVJRAde0rCYEVVemNoCS9kJvv1pcvtNLECORHF5DUVOX2FyP1jjaaXQj4Znmvqq6K0kCgYAFNWx1uJsvSSHjuyPcLKVjrmt7/CESSzG6HPhb0osikkdwGjv7d7uOfWDDVIol1I/NEm0MUiwl2VidRl3EH+IOu7GXC53WdUqrmXb11CHynxKaOuHqlh3hQMh15BAxpJVia8qYPkzVI5hyB/lxZUt7EU0/6MCmJ1nRubMpryyBvw==",
			"json",
			"utf-8",
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlOskvwmzUUrOxMbECsUhxSMlWWY0uTRquWx290gCvtH5oi92mj6JRek2w4YDZszFyElfjOs9KvX3dHsaDXDS3kwV+rg7Drqwz+LA5tCHPZCaaQPt0ljp4vccmAthdeVWmNd9wBd8LB055bUT9kOJdDagamkAQQLHsoNdQXIW2GdgyzglWIyulJIKtbOVZPOVLP74LuNIVdKjK7TEbU1Axp+6nSC7ymi6c0OHq2Cl9aWFfblWdiLElkpkZ/RhwAp4RQo9kXY6vG29hzCSSby9VD62D0iPgUuuX/OlH74/vIdKntORERmKWlj24GUc9nuamqDLKiJi7bB4CXBVn4teLQIDAQAB",
			"RSA2"
			);
	
	//沙箱测试 下单
	public static String getOrderStrDev(AliAppPayQO aliAppPayQO) {
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		request.setNotifyUrl("http://xxxxxxxx:8001/alipayCallBack/test");
		request.setBizContent("{" +
//		"\"timeout_express\":\"90m\"," +
		"\"total_amount\":"+"\""+ aliAppPayQO.getTotalAmount()+"\""+","+
		"\"product_code\":\"QUICK_MSECURITY_PAY\"," +
//		"\"body\":\"Iphone6 16G\"," +
		"\"subject\":"+"\""+ aliAppPayQO.getSubject()+"\""+","+
		"\"out_trade_no\":"+"\""+ aliAppPayQO.getOutTradeNo()+"\""+","+
				"  }");
		AlipayTradeAppPayResponse response;
		try {
			response = sandBoxCli.sdkExecute(request);
		} catch (AlipayApiException e) {
			throw new LeaseException("沙箱支付宝下单异常");
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

	//初始化客户端
	private static final CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
	static{
		certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");  //gateway:支付宝网关（固定）https://openapi.alipay.com/gateway.do
		certAlipayRequest.setAppId("2016112203111432");  //APPID 即创建应用后生成,详情见创建应用并获取 APPID
		certAlipayRequest.setPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCly2k9k/tI2WTnrpkHE8GFMLyuahHpgKtfZ/klRwjTnZJyKiVjRQamhnLWwB7SB3gM+69R9mdlBXruNjXD4ODorUwF/pYyO8zwSn0lbFRHBLI+aRNa82LDdhAAsUQnupsm7IydOjskr6+qkL1u4B5F2JYrHPvzmQxCh07LULnJGmb5Su/WAtJ2Y/lkH0lZPj14oOzrYQSoVEwHpM7F8oAYRcblewpZDilJ8BVQOe3tQqjnc1SSN0aNyDv28yrzwDX0UEARknwdTEXSNppLEd7LCNSSP3fBCrdykm5aAmEXiI3TibsRHVSD2PeDORW9Mgt9VVSynM7DUDP29mHS6k5LAgMBAAECggEAKNk0JcLXD7zYOqju9Spt7LX0Vvi1TxWvo4MHhKK6zwCsu0odChdQqyjHFJgjP9YtQu2j5YrTMLWn8xcSKh+26j0AVR3Dvb60IKQJr3XAUfsbO3tCFI+Qzt3A1ZrXUv6MCYwQnRBi0AglPN4Kfk3jx4u0fAzZGsyjk6SoyjkqfRyJk6w6EhQeeFDsAPsoWEXcKVA474fhLVpFG5rP1soviTHgrpHrx2Cjr5UYFYLEFMyOEhU03GCzVZ2t+fXeDXDuBL0+jub+JEKsu/asaulszggn5JlR5Q1f1+e6A9gJvLrGQm5CwcwiOVF4lo3EhiLzDGKaSq9g+vg3UMr3O4TC4QKBgQDXDTGEA3NmYrRj5JgKvM3MYa/xq85xD9x5Rb6juHIHFTU7rIlZ/iKAcu5OHeplOvgYL3TL1YqiLN4wU2oHzqArbWoLv1eiYiOc6LKi2QffHzCOUcx9jYIQVFOUgblXQbHbTDLLp/Q/eUDwIcS2S7tyjsCl5UQrHVVGAo0nn4bqEQKBgQDFXSjiG1xAZCwRMftvlAyNc0VasgMZczgY7kLELiVB7Y9A8BFoa1bJSkmOVVHGoWFlynndrN/jTKb01IMg11wjO7a7VZ0trQD5+yu3HXl5nZenQKLRYub83q0qrVXGrhVu5AwpxQnA8VvMuB/mUU2caiOhcr5x/K84dEGHFis2mwKBgElWuII64woRs0810BwpU03WKriSTO8F57x76o3PAyQjOWpGZvkH0CBaEacWUK+BMsacuIojIwo3nvaAG/LZpono4HdtI6e/LEIAoZsjxKl064w9nS1cKvUCrRZCP0DgqJUPIWuOtZ9H7Lt36Kv6m8xvnod07pB9im07xYOzxcDhAoGAQLwlxMQkpho1Og6i4h41X9AHV5A75AUeyU6dJ6vbHJzG+A3GJ/HNLYjoR39Eq1oTkEexYTxq0ys5N+TcfdElr4jOjvOt8mVkhJoV5KIOTg2ayIky3msKSLbhJbZlBB3ledHjC3BNmrOP4L4j+G+CfWNy/GmefdTbxDCdralf0zECgYEAyfTJelg/KzoaEQdQ2eost2aRJQ6ZDrOKsiCq/888xGMfRQe7nuOJddf8V/yvHsgZfstlNkgdYPeAo4BFGMzm+2Pb4I2VSPb3uv0hyLL2U7zWANDVlwx7RuLdjkv+/fSMqrQ04tydY+YIusnzrI+1KpjqsFyd+Pbtd+3kxxVf7LA=");  //开发者应用私钥，由开发者自己生成
		certAlipayRequest.setFormat("json");  //参数返回格式，只支持 json 格式
		certAlipayRequest.setCharset("utf-8");  //请求和签名使用的字符编码格式，支持 GBK和 UTF-8
		certAlipayRequest.setSignType("RSA2");  //商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐商家使用 RSA2。   
		certAlipayRequest.setCertPath("E:/ali_cert/appCertPublicKey_2021002119679359.crt"); //应用公钥证书路径（app_cert_path 文件绝对路径）
		certAlipayRequest.setAlipayPublicCertPath("E:/ali_cert/alipayCertPublicKey_RSA2.crt"); //支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
		certAlipayRequest.setRootCertPath("E:/ali_cert/alipayRootCert.crt");  //支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
//		certAlipayRequest.setCertPath("/mnt/db/smart-community/cert/ali_cert/appCertPublicKey_2021002119679359.crt"); //应用公钥证书路径（app_cert_path 文件绝对路径）
//		certAlipayRequest.setAlipayPublicCertPath("/mnt/db/smart-community/cert/ali_cert/alipayCertPublicKey_RSA2.crt"); //支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
//		certAlipayRequest.setRootCertPath("/mnt/db/smart-community/cert/ali_cert/alipayRootCert.crt");  //支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
	}
	
	public static void main(String[] args) throws AlipayApiException {
		AliAppPayQO aliAppPayVO = new AliAppPayQO();
		aliAppPayVO.setOutTradeNo(String.valueOf(SnowFlake.nextId()));
		aliAppPayVO.setTotalAmount(new BigDecimal(100));
		aliAppPayVO.setSubject("支付租金");
//		System.out.println(getOrderStrDev(aliAppPayVO));//测试
//		transferByCert();
//		queryBalance();
//		queryPay();
//		System.out.println(test111());
	}
	
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
	public static void transferByCert() throws AlipayApiException{
		AlipayClient alipayClient = new DefaultAlipayClient(certAlipayRequest);
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
	public String getOrderStr(AliAppPayQO aliAppPayQO) {
		AlipayClient alipayClient = null;
		try {
			alipayClient = new DefaultAlipayClient(certAlipayRequest);
		} catch (AlipayApiException e1) {
			e1.printStackTrace();
			throw new LeaseException("支付初始化出错");
		}
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		request.setNotifyUrl("http://xxxxxxxx:8001/alipayCallBack/test");
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
	
	//处理回调订单
	public void dealOrder(AiliAppPayRecordEntity order){
		logger.error("开始处理订单：" + order.getOrderNo());
		if(PaymentEnum.TradeNameEnum.RENT_PAYMENT.getIndex().equals(order.getTradeType())){  // 充值
			logger.error("开始处理充值订单");
			//支付订单修改状态完成
			boolean completeAliAppPayRecord = ailiAppPayRecordServiceImpl.completeAliAppPayRecord(order.getOrderNo());
			logger.error("订单状态修改完成" + completeAliAppPayRecord + "订单号：" + order.getOrderNo());
		}else if(PaymentEnum.TradeNameEnum.RENT_WITHDRAWAL.getIndex().equals(order.getTradeType())){  // 提现
			logger.error("开始处理提现订单");
			logger.error("提现订单处理完成");
		}
	}
	
}
