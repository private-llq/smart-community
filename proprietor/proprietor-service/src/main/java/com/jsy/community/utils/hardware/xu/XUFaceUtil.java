package com.jsy.community.utils.hardware.xu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.dto.face.xu.XUFaceEditPersonDTO;
import com.jsy.community.utils.MyHttpUtils;
import org.apache.http.client.methods.HttpPost;

import java.util.Map;

/**
 * @author chq459799974
 * @description 炫优人脸识别二维码刷卡一体机(mqtt) 工具类
 * @since 2021-01-29 16:48
 **/
public class XUFaceUtil {
	
	/**
	 * 人员添加
	 */
	public static boolean editPerson(XUFaceEditPersonDTO xUFaceEditPersonDTO){
		Map bodyMap = JSONObject.parseObject(JSON.toJSONString(xUFaceEditPersonDTO), Map.class);
		HttpPost httpPost = MyHttpUtils.httpPostWithoutParams("http://192.168.12.59:9363/face/xu/testEditPersonBase64", bodyMap);
		MyHttpUtils.setDefaultHeader(httpPost);
		String httpResult = (String)MyHttpUtils.exec(httpPost, MyHttpUtils.ANALYZE_TYPE_STR);
		JSONObject result = JSONObject.parseObject(httpResult);
		return result != null && "0".equals(result.getString("code"));
	}
	
}
