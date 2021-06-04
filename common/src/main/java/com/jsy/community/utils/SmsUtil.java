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
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Component;

/**
* @Description: 阿里云短信服务
 * @Author: chq459799974
 * @Date: 2020/12/11
**/
@Slf4j
public class SmsUtil {
    /**
     * 调用阿里云短信接口
     */
    public static void sendSmsCode(String mobile,String signName,String templateName,String jsonContent) {
        String regionId = "";
        String accessKeyId = ConstClasses.AliYunDataEntity.smsAccessKeyId;
        String secret = ConstClasses.AliYunDataEntity.smsSecret;
        
        DefaultProfile profile = DefaultProfile.getProfile(regionId,accessKeyId,secret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateName);
        request.putQueryParameter("TemplateParam", jsonContent);
        try {
        	CommonResponse  response = client.getCommonResponse(request);
            if(response != null && response.getData() != null){
            	JSONObject parseObject = JSONObject.parseObject(response.getData());
                System.out.println(parseObject);
            	if("OK".equals(parseObject.getString("Message")) && "OK".equals(parseObject.getString("Code"))){
                    log.info("向" + mobile + "发送短信成功");
                    log.info("短信内容：\n" + jsonContent);
            	}else{
                    throw new JSYException(JSYError.INTERNAL.getCode(),"短信发送失败");
                }
            }
        } catch (ServerException e) {
            log.info("向" + mobile + "发送短信失败");
            e.printStackTrace();
            throw new JSYException(JSYError.INTERNAL);
        } catch (ClientException e) {
            log.info("向" + mobile + "发送短信失败");
            e.printStackTrace();
            throw new JSYException(JSYError.INTERNAL);
        }
    }
    
    /**
     * 发送验证码
     */
    public static String sendVcode(String mobile){
        String code = MyMathUtils.randomCode(4);
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.VCODE,JSON.toJSONString(map));
        return code;
    }
    
    /**
     * 物业端添加操作员-发送初始密码
     */
    public static void sendSmsPassword(String mobile,String password) {
        Map<String,String> map = new HashMap<>();
        map.put("phonenumber",mobile);
        map.put("password",password);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.ADD_OPERATOR,JSON.toJSONString(map));
    }
    
}
