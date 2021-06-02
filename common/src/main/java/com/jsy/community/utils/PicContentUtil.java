package com.jsy.community.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.constant.DrivingLicense;
import com.jsy.community.exception.JSYException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

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
	private static volatile DrivingLicense drivingLicense;
	
	//身份证照片内容识别
	public static Map<String,Object> getIdCardPicContent(String picBase64,String type){
		String url = ConstClasses.AliYunDataEntity.URL_ID_CARD_OCR;
		//新建请求，设置参数
		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("image",picBase64);
		JSONObject configObject = new JSONObject();
		configObject.put("side",type);
		bodyMap.put("configure",configObject);
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url,bodyMap);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Authorization","APPCODE " + ConstClasses.AliYunDataEntity.appCode);
		MyHttpUtils.setHeader(httpPost,headers);
		MyHttpUtils.setDefaultHeader(httpPost);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		//执行请求，返回结果
		String httpResult = (String)MyHttpUtils.exec(httpPost,MyHttpUtils.ANALYZE_TYPE_STR);
		//解析结果
		JSONObject result = JSONObject.parseObject(httpResult);
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
	/**
	 * 行驶证识别
	 * @author YuLF
	 * @since  2021/2/25 10:02
	 * @Param	drivingLicenseImageUrl	行驶证图片
	 * @return	返回车牌、车辆类型、行驶证图片
	 */
	public static Map<String, Object> getDrivingLicenseContent(String drivingLicenseImageUrl){
		Map<String, String> headers = new HashMap<>(2);
		drivingLicense = getDrivingLicenseInstance();
		headers.put("Authorization", "APPCODE " + drivingLicense.getAppCode());
		//根据API的要求，定义相对应的Content-Type
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		Map<String, String> queryParam = new HashMap<>(1);
		Map<String, String> bodyParam = new HashMap<>(2);
		//行驶证图片Base64字符串或者URL地址
		bodyParam.put("pic", drivingLicenseImageUrl);
		//行驶证正副本（1:正本 2:副本）
		bodyParam.put("type", "1");
		try {
			HttpResponse response = HttpUtils.doPost(drivingLicense.getApi(), drivingLicense.getPath(), drivingLicense.getMethod(), headers, queryParam, bodyParam);
			//获取response的body
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity()));
			Integer retCode = jsonObject.getInteger("ret");
			//识别失败
			if(!retCode.equals(DrivingLicense.ErrorCode.IDENTIFY_SUCCESS.getCode())){
				DrivingLicense.ErrorCode errorCode = DrivingLicense.ErrorCode.valueOf(retCode);
				throw new JSYException(errorCode.getCode(), errorCode.getMsg());
			}
			//识别成功 取值
			JSONObject dataObj = JSON.parseObject(jsonObject.getString("data"));
			Map<String, Object> resultMap = new HashMap<>(3);
			//车牌
			resultMap.put("carPlate", dataObj.getString("lsnum"));
			//车辆类型
			String lsTypeText = dataObj.getString("lstype");
			BusinessEnum.CarTypeEnum carTypeEnum = BusinessEnum.CarTypeEnum.getContainsType(lsTypeText);
			HashMap<Object, Object> carTypeMap = new HashMap<>(1);
			carTypeMap.put(carTypeEnum.getCode(), carTypeEnum.getName());
			resultMap.put("carType", carTypeMap);
			//行驶证图片
			resultMap.put("drivingLicenseImageUrl", drivingLicenseImageUrl);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DrivingLicense getDrivingLicenseInstance(){
		if(drivingLicense == null){
			synchronized (PicContentUtil.class){
				if(drivingLicense == null){
					drivingLicense = (DrivingLicense) SpringContextUtils.getBean("drivingLicense");
				}
			}
		}
		return drivingLicense;
	}



}
