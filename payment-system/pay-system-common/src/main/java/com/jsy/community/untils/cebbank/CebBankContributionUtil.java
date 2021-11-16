package com.jsy.community.untils.cebbank;

import com.google.gson.Gson;
import com.jsy.community.config.service.CebBankEntity;
import com.jsy.community.constant.CebBankConst;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResonseModel;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费工具类
 * @Date: 2021/11/10 16:18
 * @Version: 1.0
 **/
public class CebBankContributionUtil {

    /**
     * @author: Pipi
     * @description: 注册
     * @param cebLoginQO: 
     * @return: java.lang.String
     * @date: 2021/11/12 10:18
     **/
    public static String login(CebLoginQO cebLoginQO) {
        return sendRequest(cebLoginQO, cebLoginQO.getDeviceType(), CebBankConst.LOGIN);
    }

    /**
     * @author: Pipi
     * @description: 查询城市
     * @param : 
     * @return: java.lang.String
     * @date: 2021/11/12 10:19
     **/
    public static String queryCity(CebQueryCityQO cebQueryCityQO) {
        return sendRequest(cebQueryCityQO, cebQueryCityQO.getDeviceType(), CebBankConst.QUERY_CITY);
    }

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
     * @return: java.lang.String
     * @date: 2021/11/12 10:57
     **/
    public static String queryCityContributionCategory(CebQueryCityContributionCategoryQO categoryQO) {
        return sendRequest(categoryQO, categoryQO.getDeviceType(), CebBankConst.QUERY_CITY_CONTRIBUTION_CATEGORY);
    }
    
    /**
     * @author: Pipi
     * @description: 查询缴费类别下缴费项目
     * @param projectQO: 
     * @return: java.lang.String
     * @date: 2021/11/12 11:08
     **/
    public static String queryContributionProject(CebQueryContributionProjectQO projectQO) {
        return sendRequest(projectQO, projectQO.getDeviceType(), CebBankConst.QUERY_CONTRIBUTION_PROJECT);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费账单信息
     * @param billInfoQO:
     * @return: java.lang.String
     * @date: 2021/11/12 11:33
     **/
    public static String queryBillInfoQO(CebQueryBillInfoQO billInfoQO) {
        return sendRequest(billInfoQO, billInfoQO.getDeviceType(), CebBankConst.QUERY_BILL_INFO);
    }

    /**
     * @author: Pipi
     * @description: 查询手机充值缴费账单
     * @param billQO:
     * @return: java.lang.String
     * @date: 2021/11/12 11:46
     **/
    public static String queryMobileBill(CebQueryMobileBillQO billQO) {
        return sendRequest(billQO, billQO.getDeviceType(), CebBankConst.QUERY_MOBILE_BILL);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录
     * @param recordQO:
     * @return: java.lang.String
     * @date: 2021/11/12 11:53
     **/
    public static String queryContributionRecord(CebQueryContributionRecordQO recordQO) {
        return sendRequest(recordQO, recordQO.getDeviceType(), CebBankConst.QUERY_CONTRIBUTION_RECORD);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录详情
     * @param infoQO:
     * @return: java.lang.String
     * @date: 2021/11/12 14:51
     **/
    public static String queryContributionRecordInfo(CebQueryContributionRecordInfoQO infoQO) {
        return sendRequest(infoQO, infoQO.getDeviceType(), CebBankConst.QUERY_CONTRIBUTION_RECORD_INFO);
    }



    /**
     * @author: Pipi
     * @description: 光大银行云缴费发送请求
     * @param object: 接口对应参数对象
     * @param deviceType: 请求端
     * @param transacCode: 接口名称
     * @return: void
     * @date: 2021/11/11 11:57
     **/
    public static String sendRequest(Object object, String deviceType, String transacCode) {
        Gson gson = new Gson();
        String reqdata_json = gson.toJson(object);
        String siteCode = CebBankEntity.siteCode;
        String version = CebBankEntity.version;
        String charset = CebBankEntity.charset;
        try {
            String reqdata = new String(Base64.encodeBase64(reqdata_json.getBytes(charset)));
            //生成签名(数字签名，采用Base64编码和MD5withRSA算法实现，签名内容顺序为：siteCode、version、transacCode、reqdata_json)
            String content = siteCode + version + transacCode + reqdata_json;
            String signature = SignUtil.getSign(CebBankEntity.privateKey, content , charset);
            //http请求参数
            Map<String, String> sendMap = new HashMap<>();
            sendMap.put("siteCode", siteCode);
            sendMap.put("version", version);
            sendMap.put("deviceType", deviceType);
            sendMap.put("transacCode", transacCode);
            sendMap.put("charset", charset);
            sendMap.put("reqdata", reqdata);
            sendMap.put("signature", signature);
            //发送请求
            CloseableHttpResponse httpResonse = HttpSendUtil.sendToOtherServer2(CebBankEntity.requestUrl, sendMap);
            //接收返回，并验签
            if(null != httpResonse){
                //根据httpResonse返回结果值，进行验签
                String respStr = StringUtil.parseResponseToStr(httpResonse);
                System.out.println("光大云缴费响应结果:" + respStr);
                HttpResonseModel httpResonseModel = gson.fromJson(respStr, HttpResonseModel.class);
                if (verifyhttpResonse(httpResonseModel)) {
                    return String.valueOf(Base64.decodeBase64(httpResonseModel.getRespData()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author: Pipi
     * @description: 根据返回结果验签,true为验签通过,false为验签失败
     * @param httpResonseModel:
     * @return: java.lang.Boolean
     * @date: 2021/11/11 15:08
     **/
    private static Boolean verifyhttpResonse(HttpResonseModel httpResonseModel) throws IOException {
        String respData = httpResonseModel.getRespData();
        String respCode = httpResonseModel.getRespCode();
        String respMsg = httpResonseModel.getRespMsg();
        String signature = httpResonseModel.getSignature();

        byte[] decodeBase64 = Base64.decodeBase64(respData);
        String respData_json = new String(decodeBase64);
        System.out.println("返回应答，respCode=" + respCode);
        System.out.println("返回应答，respMsg=" + respMsg);
        System.out.println("返回应答，signature=" + signature);
        System.out.println("返回应答，respData=" + respData_json);

        String content = respCode+respMsg+respData_json;
        //验签
        return VerifyUtil.verify(CebBankEntity.publicKey, signature, content, "utf-8");
    }
}
