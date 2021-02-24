package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 图片文字识别
 * @since 2021-02-24 15:13
 **/
public class PicContentUtil {
	
	public static final String ID_CARD_PIC_SIDE_FACE = "face";
	public static final String ID_CARD_PIC_SIDE_BACK = "back";
	
	//身份证照片内容识别
	public static Map<String,Object> getIdCardPicContent(String picBase64,String type){
		String appCode = "17e38ac209824aab9d0e82097f59ba11";
		String url = "https://dm-51.data.aliyun.com/rest/160601/ocr/ocr_idcard.json";
		//新建请求，设置参数
		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("image",picBase64);
		bodyMap.put("configure",type);
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url,bodyMap);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Authorization","APPCODE " + appCode);
		MyHttpUtils.setHeader(httpPost,headers);
		MyHttpUtils.setDefaultHeader(httpPost);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		//执行请求，返回结果
		String httpResult = (String)MyHttpUtils.exec(httpPost,MyHttpUtils.ANALYZE_TYPE_STR);
		//解析结果
		JSONObject result = JSONObject.parseObject(httpResult);
		System.out.println(result);
		if(result != null && result.getBooleanValue("success")){
			Map<String, Object> returnMap = new HashMap<>();
			if(ID_CARD_PIC_SIDE_FACE.equals(type)){
				returnMap.put("name",result.getString("name"));
				returnMap.put("sex",result.getString("sex"));
				returnMap.put("num",result.getString("num"));
				returnMap.put("address",result.getString("address"));
			}else{
				returnMap.putAll(result);
			}
			return returnMap;
		}
		return null;
	}
	
}
