package com.jsy.community.utils;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.stereotype.Component;

/**
* @Description: 阿里云短信服务
 * @Author: chq459799974
 * @Date: 2020/12/11
**/
@Component
public class SmsUtil {
    //TODO 后期重载几个方法 templateName不用传
    public static Map<String,String> sendSms(String phonenumber,String templateName) {
        //TODO templateName模板名待申请
        //TODO signName签名待申请(如有需要)
        String regionId = "";
        String accessKeyId = "";
        String secret = "";
        String signName = "";
        DefaultProfile profile = DefaultProfile.getProfile(regionId,accessKeyId,secret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phonenumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateName);
        String randomCode = MyMathUtils.randomCode(4);
        request.putQueryParameter("TemplateParam", "{\"code\":".concat(randomCode).concat("}"));
        Map<String,String> resMap = null;
        try {
        	CommonResponse  response = client.getCommonResponse(request);
            if(response != null && response.getData() != null){
            	JSONObject parseObject = JSONObject.parseObject(response.getData());
            	if("OK".equals(parseObject.getString("Message")) && "OK".equals(parseObject.getString("Code"))){
            		resMap = new HashMap<>();
            		resMap.put(phonenumber, randomCode);
            	}
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return resMap;
    }
    
    private static void main(String[] args) {
    	//sendSms("15178763584",BusinessConst.SMS_LOGIN);
	}
}
