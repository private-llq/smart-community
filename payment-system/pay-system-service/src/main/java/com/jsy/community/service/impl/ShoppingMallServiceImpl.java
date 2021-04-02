package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IShoppingMallService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.MyHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区商城相关业务
 * @since 2021-04-02 16:27
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class ShoppingMallServiceImpl implements IShoppingMallService {
	
	private static final String protocolType = "http://";
	private static final String host = "192.168.12.29";
//	private static final String host = "222.178.212.29";
	private static final String port = "9927";
	private static final String path = "/services/order/pub/order/checkOrder";
	
	/**
	* @Description: 商城订单校验
	 * @Param: [orderData]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/4/2
	**/
	public Map<String,Object> validateShopOrder(Map<String,Object> orderData){
		if(orderData == null){
			throw new PaymentException(JSYError.BAD_REQUEST.getCode(),"订单数据不能为空");
		}
		Map<String, Object> returnMap = new HashMap<>();
		//url
		String url = protocolType + host + ":" + port + path;
		//组装http请求
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url, orderData);
		//设置header
		MyHttpUtils.setDefaultHeader(httpPost);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		//执行
		String httpResult;
		JSONObject result = null;
		try{
			//执行请求，解析结果
			httpResult = (String)MyHttpUtils.exec(httpPost,MyHttpUtils.ANALYZE_TYPE_STR);
			result = JSONObject.parseObject(httpResult);
			if(0 == result.getIntValue("code")){
				returnMap.put("code",0);
				log.info("商城订单校验通过");
			}else if(-1 == result.getIntValue("code")){
				returnMap.put("code",-1);
				returnMap.put("msg",result.getString("message"));
				log.error("商城订单校验不通过，订单不存在");
			}else{
				returnMap.put("code",JSYError.INTERNAL.getCode());
				returnMap.put("msg","订单校验出错");
				log.error("商城订单校验远程服务 - 远程服务出错，返回码：" + result.getIntValue("code") + " ，错误信息：" + result.getString("message"));
			}
			return returnMap;
		}catch (Exception e) {
			log.error("订单校验远程服务 - http执行或解析异常，json解析结果" + result);
			returnMap.put("code",JSYError.INTERNAL.getCode());
			returnMap.put("msg","订单校验出错");
			return returnMap;
		}
	}
	
}
