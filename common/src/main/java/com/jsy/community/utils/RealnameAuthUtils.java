package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.qo.RealnameBlinkInitQO;
import com.jsy.community.qo.RealnameBlinkQueryQO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author chq459799974
 * @description 实名认证
 * @since 2020-12-11 15:57
 **/
public class RealnameAuthUtils {
	
	/**
	* @Description: 实名认证 二要素
	 * from https://market.aliyun.com/products/57000002/cmapi022049.html
	 * @Param: [name, idCard]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/11
	**/
	public static boolean twoElements(String name, String idCard){
		
		String appCode = "xxxxxxxxxxxxxxxxxxxxx";
		String url = "https://idcert.market.alicloudapi.com/idcard";
		//新建请求，设置参数
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("name",name);
		paramsMap.put("idCard",idCard);
		HttpGet httpGet = MyHttpUtils.httpGet(url,paramsMap);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Authorization","APPCODE " + appCode);
		MyHttpUtils.setHeader(httpGet,headers);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpGet);
		//执行请求，返回结果
		String httpResult = (String)MyHttpUtils.exec(httpGet,MyHttpUtils.ANALYZE_TYPE_STR);
		//解析结果
		JSONObject result = JSONObject.parseObject(httpResult);
		if(result != null && "01".equals(result.getString("status"))){
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 实名认证 三要素 (眨眼版) 前置接口
	 * @Param: [realnameBlinkInitQO]
	 * @Return: com.alibaba.fastjson.JSONObject
	 * @Author: chq459799974
	 * @Date: 2021/3/2
	**/
	public static JSONObject initBlink(RealnameBlinkInitQO realnameBlinkInitQO){
		String appCode = "453d3d5bf1364eba89718adabf796f27";
		String host = "https://ediszim.market.alicloudapi.com";
		String path = "/zoloz/zim/init";
		String method = "POST";
		//params参数
		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("identityParam",realnameBlinkInitQO.getIdentityParam());
		bodyMap.put("metaInfo",realnameBlinkInitQO.getMetaInfo());
		bodyMap.put("packageName",realnameBlinkInitQO.getPackageName());
		bodyMap.put("platform",realnameBlinkInitQO.getPlatform());
		Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "APPCODE " + appCode);
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		JSONObject result = null;
		try {
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodyMap);
			result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	* @Description: 实名认证 三要素 (眨眼版) 查询结果 (认证操作由APP端完成)
	 * @Param: [realnameBlinkQueryQO]
	 * @Return: com.alibaba.fastjson.JSONObject
	 * @Author: chq459799974
	 * @Date: 2021/3/2
	**/
	public static JSONObject getBlinkResult(RealnameBlinkQueryQO realnameBlinkQueryQO){
		String appCode = "453d3d5bf1364eba89718adabf796f27";
		String host = "https://ediszim.market.alicloudapi.com";
		String path = "/zoloz/zim/getResult";
		String method = "POST";
		//params参数
		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("bizId",realnameBlinkQueryQO.getBizId());
		bodyMap.put("certifyId",realnameBlinkQueryQO.getCertifyId());
		Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "APPCODE " + appCode);
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		JSONObject result = null;
		try {
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodyMap);
			result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	
	
	
	/**
	* @Description: 实名认证 三要素 (读数版) 前置接口
	 * @Param: []
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/2/23
	**/
	public static Map<String,String> getReadAloudNumber(){
		String host = "https://edis3v.market.alicloudapi.com/getBehavior";
		String path = "/verify";
		String method = "POST";
		String appcode = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		Map<String, String> headers = new HashMap<>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		//防重放校验
		headers.put("X-Ca-Nonce", UUID.randomUUID().toString());
		Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> bodys = new HashMap<String, String>();
		Map<String, String> returnMap = new HashMap<>();
		try {
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//			System.out.println(response.toString());
			//获取response的body
			System.out.println(EntityUtils.toString(response.getEntity()));
			JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
			if(result != null){
				returnMap.put("code",result.getString("code"));
				returnMap.put("msg",result.getString("msg"));
				// 调用成功返回供用户朗读的数字，失败不返回
				if("0000".equals(result.getString("code"))){
					returnMap.put("number",result.getString("behavior"));
				}
				return returnMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("code", "-1");
		returnMap.put("msg","未取得远程数据，实名认证初始化失败");
		return returnMap;
	}
	
	/**
	* @Description: 实名认证 三要素 (读数版) 需要先调生成behaviorToken的接口，并获得读数  暂定微信小程序用
	 * from https://market.aliyun.com/products/57000002/cmapi00037639.html
	 * @Param: [name, idCard, netFileUrl, behaviorToken]
	 * @Return: java.util.Map<java.lang.String,java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/2/23
	**/
	public static Map<String,String> threeElementsNumber(String name, String idCard, String netFileUrl, String behaviorToken){
		String host = "https://edis3v.market.alicloudapi.com";
		String path = "/verify";
		String method = "POST";
		String appcode = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		Map<String, String> headers = new HashMap<>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		//根据API的要求，定义相对应的Content-Type
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		//防重放校验
		headers.put("X-Ca-Nonce", UUID.randomUUID().toString());
		Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> bodys = new HashMap<String, String>();
		bodys.put("behaviorToken", behaviorToken);
		bodys.put("certName", name);
		bodys.put("certNo", idCard);
		String base64Str = Base64Util.netFileToBase64(netFileUrl);
		bodys.put("video", base64Str);
		Map<String, String> returnMap = new HashMap<>();
		try {
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//			System.out.println(response.toString());
			//获取response的body
			System.out.println(EntityUtils.toString(response.getEntity()));
			JSONObject result = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
			if(result != null){
				returnMap.put("code",result.getString("code"));
				returnMap.put("msg",result.getString("msg"));
				return returnMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("code", "-1");
		returnMap.put("msg","未取得远程数据，实名认证失败");
		return returnMap;
	}
	
}
