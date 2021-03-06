package com.jsy.community.utils;
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
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

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
     * 发送验证码-通用模板
     */
    public static String sendVcode(String mobile, Integer length, String signName){
        if(length == null){
            //默认给四位数验证码
            length = BusinessConst.SMS_VCODE_LENGTH_DEFAULT;
        }
        String code = MyMathUtils.randomCode(length);
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
//        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.VCODE,JSON.toJSONString(map));
        sendSmsCode(mobile,signName,Const.SMSTemplateName.VCODE,JSON.toJSONString(map));
        return code;
    }
    
    /**
     * 发送验证码-签章忘记密码
     */
    public static String forgetPasswordOfSign(String mobile, Integer length, String signName){
        if(length == null){
            //默认给四位数验证码
            length = BusinessConst.SMS_VCODE_LENGTH_DEFAULT;
        }
        String code = MyMathUtils.randomCode(length);
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
//        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.VCODE_SIGN_FORGET_PASSWORD,JSON.toJSONString(map));
        sendSmsCode(mobile,signName,Const.SMSTemplateName.VCODE_SIGN_FORGET_PASSWORD,JSON.toJSONString(map));
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
    
    /**
     * 物业端操作员-重置密码
     */
    public static void resetPassword(String mobile,String password) {
        Map<String,String> map = new HashMap<>();
        map.put("phonenumber",mobile);
        map.put("password",password);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.RESET_PASSWORD,JSON.toJSONString(map));
    }
    
    /**
     * 物业端-公告通知
     */
    public static void propertyNotice(String mobile,String content) {
        Map<String,String> map = new HashMap<>();
        map.put("phonenumber",mobile);
        map.put("content",content);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.PROPERTY_NOTICE,JSON.toJSONString(map));
    }
    
    /**
     * 物业端-意见报修通知
     */
    public static void repairNotice(String mobile, String content, String tel) {
        Map<String,String> map = new HashMap<>();
        map.put("phonenumber",mobile);
        map.put("content",content);
        map.put("tel",tel);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.REPAIR_NOTICE,JSON.toJSONString(map));
    }
    
    /**
     * 物业端-挪车通知
     */
    public static void moveCarNotice(String mobile, String carNum, String linkWay) {
        Map<String,String> map = new HashMap<>();
        map.put("phonenumber",mobile);
        map.put("car_num",carNum);
        map.put("linkway",linkWay);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.MOVE_CAR_NOTICE,JSON.toJSONString(map));
    }
    
    /**
     * 物业端-车禁月租到期通知
     */
    public static void expirationNotice(String mobile, String carNum, String linkWay) {
        Map<String,String> map = new HashMap<>();
        map.put("car_num",carNum);
        map.put("linkway",linkWay);
        sendSmsCode(mobile,Const.SMSSignName.SIGN_COMPANY,Const.SMSTemplateName.EXPIRATION_NOTICE,JSON.toJSONString(map));
    }
    
}
