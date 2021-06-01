package com.jsy.community.utils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.jsy.community.constant.ConstClasses;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Component;

/**
* @Description: 阿里云短信服务
 * @Author: chq459799974
 * @Date: 2020/12/11
**/
@Component
public class SmsUtil {
    //TODO 后期重载几个方法 templateName不用传
    public static Map<String,String> sendSmsCode(String phonenumber,String templateName) {
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
    
    //发送初始密码
    public static boolean sendSmsPassword(String phonenumber,String password) {
        String url = "http://smsbanling.market.alicloudapi.com/smsapis";
//        String msg = "账号：" + phonenumber +"，登录密码：" + password + "。您的物业管理后台账号已创建，请妥善保管账号资料，可登录系统设置新密码。";
	    String msg = "账号：" + phonenumber +"，登录密码" + password + "。您的物业管理后台账号已创建，请妥善保管账号资料，可登录系统设置新密码。";
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "APPCODE " + ConstClasses.AliYunDataEntity.appCode);
        Map<String, String> queryParam = new HashMap<>(2);
        queryParam.put("mobile",phonenumber);
        queryParam.put("msg",msg);
        queryParam.put("sign","智慧社区");
    
        //发送短信
        HttpGet httpGet = MyHttpUtils.httpGet(url,queryParam);
        MyHttpUtils.setHeader(httpGet,headers);
        String result = (String) MyHttpUtils.exec(httpGet,1);
        //验证结果
        if ( Objects.isNull(result) ){
            return false;
        }
        Integer resultCode = JSON.parseObject(result).getInteger("result");
        if( resultCode != 0 ){
            return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        sendSmsPassword("15178763584","abc123456");
	}
}
