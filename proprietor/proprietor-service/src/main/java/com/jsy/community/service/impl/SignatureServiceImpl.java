package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.entity.RedbagEntity;
import com.jsy.community.utils.MapBeanUtil;
import com.jsy.community.utils.MyHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.Map;

/**
 * @author chq459799974
 * @description 签章相关
 * @since 2021-02-23 17:38
 **/
@Slf4j
//@DubboService(version = Const.version, group = Const.group_proprietor)
public class SignatureServiceImpl {
	
	private static final String protocolType = "http://";
		private static final String host = "192.168.12.37";
//	private static final String host = "222.178.212.29";
	private static final String port = "10005";
	
	//POST 新增用户信息
	public Map<String,Object> insertUser(){
		return null;
	}
	
	//POST 批量新增用户信息(物业端批量导入时)
	public Map<String,Object> batchInsertUser(){
		return null;
	}
	
	//PUT 实名认证后修改签章用户信息
	public Map<String,Object> realNameUpdateUser(){
		return null;
	}
	
	//PUT 修改用户普通信息
	public Map<String,Object> updateUser(){
		return null;
	}
	
	/**
	 * http调用红包/转账接口
	 */
	private boolean sendRedbagByHttp(int type, SignatureUserDTO signatureUserDTO){
		HttpPost httpPost = null;
		HttpPut httpPut = null;
		String url = "";
		switch(type){
			case 1:
				url = protocolType + host + ":" + port + "/user/insertUser";
				break;
			case 2:
				url = protocolType + host + ":" + port + "/user/batchInsertUser";
				break;
			case 3:
				url = protocolType + host + ":" + port + "/user/RealNameUpdateUser";
				break;
			case 4:
				url = protocolType + host + ":" + port + "/user/updateUser";
				break;
		}
		//获取加密对象
//		OpenParam openParam = AESUtil.returnOpenParam(redbagQO);
		//组装请求body
//		Map<String, Object> bodyMap = JSONObject.parseObject(JSON.toJSONString(openParam), Map.class);
		if(type == 1 || type == 2){
			httpPost = MyHttpUtils.httpPostWithoutParams(url, MapBeanUtil.object2Map(signatureUserDTO));
		}else if(type == 3 || type == 4){
			httpPut = MyHttpUtils.httpPutWithoutParams(url, MapBeanUtil.object2Map(signatureUserDTO));
		}
		//设置默认header
		MyHttpUtils.setDefaultHeader(httpPost != null ? httpPost : httpPut);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost != null ? httpPost : httpPut);
		JSONObject result = null;
		String httpResult = null;
		try{
			//执行请求，解析结果
			httpResult = (String)MyHttpUtils.exec(httpPost != null ? httpPost : httpPut,MyHttpUtils.ANALYZE_TYPE_STR);
			result = JSONObject.parseObject(httpResult);
			System.out.println(result);
			return true;
		}catch (Exception e) {
			log.error("签章用户远程服务 - 调用或解析出错，json解析结果" + result);
			e.printStackTrace();
			return false;
		}
	}
	
}
