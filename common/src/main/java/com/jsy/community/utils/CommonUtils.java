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
	
	public static final String PUSH_URL = "http://192.168.12.54:20002/imLogsMessage/jPush";
	
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
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams("http://"+ LanIpResolver.getLanIpByMac("3c-7c-3f-4b-c0-a0") +":2002/imLogsMessage/jPush", bodyMap);
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
		String houseReserveTitle = "房屋预约信息";
		String houseReserveContent = "张大锤在1.16号上午10点对您发布的[江北-观音桥 三钢二路8号]进行了预约！请及时回复。";
		String favoriteNoticeTitle = "您的房屋被收藏啦!";
		String favoriteNoticeContent = "张大锤在 2021-1-15 10:52:34 收藏您发布的房屋[江北-观音桥 三钢二路8号]";
		pushCommunityMSG(1,"120c83f76087f89f525",favoriteNoticeTitle,favoriteNoticeContent);
	}
	
}
