package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 公共工具
 * @since 2021-01-13 14:29
 **/
@Slf4j
public class CommonUtils {
	public static final int PUSH_TYPE_ID = 1;
	public static final int PUSH_TYPE_TAG = 2;
	
	public static final String PUSH_URL = "http://192.168.12.105:20002/imLogsMessage/jPush";
	
	/**
	* @Description: 推送社区消息http调用
	 * @Param: [pushType, tag, title, content]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021/1/13
	**/
	public static boolean pushCommunityMSG(int pushType, String tag, String title,String content){
		//组装请求body
		Map<String, Object> bodyMap = null;
		if(PUSH_TYPE_ID == pushType){ //id方式推送
			bodyMap = new HashMap<>();
			bodyMap.put("sendType","id");
		}else if(PUSH_TYPE_TAG == pushType){ //tag方式推送
			bodyMap = new HashMap<>();
			bodyMap.put("sendType","tag");
		}else{
			return false;
		}
		//加密id或tag
		tag = AESOperator.encrypt(tag);
		//获取系统时间
		Long time = System.currentTimeMillis();
		//得到MD5签名
		String sign = MD5Util.getSign(tag, time);
		//添加body参数
		bodyMap.put("value",tag); //加密后tag值
		bodyMap.put("time",time); //系统时间
		bodyMap.put("signature",sign); //MD5签名
		bodyMap.put("msgTitle",title); //推送标题
		bodyMap.put("msgContent",content); //推送内容
		//组装http请求
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(PUSH_URL, bodyMap);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		MyHttpUtils.setHeader(httpPost,headers);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		String httpResult = null;
		JSONObject result = null;
		try{
			//执行请求，解析结果
			httpResult = MyHttpUtils.exec(httpPost);
			result = JSONObject.parseObject(httpResult);
		}catch (Exception e) {
			//http请求结果httpResult工具类已打印这里不再打印
			log.error("推送社区消息 - 执行或解析出错，json解析结果" + result);
			e.printStackTrace();
		}
		if(result != null){
			return result.getBooleanValue("success");
		}
		return false;
	}
	
	public static void main(String[] args) {
		pushCommunityMSG(1,"1104a8979209ab19cf1","哎呀试一下","这是http调用");
	}
	
}
