package com.jsy.community.untils.cebbank;

import com.google.gson.Gson;
import com.jsy.community.config.service.CebBankEntity;
import com.jsy.community.constant.CebBankConst;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.cebbank.*;
import com.jsy.community.qo.unionpay.HttpResponseModel;
import com.jsy.community.vo.cebbank.test.HttpRequestModel;
import com.zhsj.basecommon.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费工具类
 * @Date: 2021/11/10 16:18
 * @Version: 1.0
 **/
@Slf4j
public class CebBankContributionUtil {

    /**
     * @author: Pipi
     * @description: 注册
     * @param cebLoginQO: 
     * @return: java.lang.String
     * @date: 2021/11/12 10:18
     **/
    public static HttpResponseModel login(CebLoginQO cebLoginQO) {
        String deviceType = cebLoginQO.getDeviceType();
        cebLoginQO.setCanal(CebBankEntity.siteCode);
        cebLoginQO.setDeviceType(null);
        return sendRequest(cebLoginQO, deviceType, CebBankConst.LOGIN);
    }

    /**
     * @author: Pipi
     * @description: 查询城市
     * @param : 
     * @return: java.lang.String
     * @date: 2021/11/12 10:19
     **/
    public static HttpResponseModel queryCity(CebQueryCityQO cebQueryCityQO) {
        String deviceType = cebQueryCityQO.getDeviceType();
        cebQueryCityQO.setCanal(CebBankEntity.siteCode);
        cebQueryCityQO.setDeviceType(null);
        return sendRequest(cebQueryCityQO, deviceType, CebBankConst.QUERY_CITY);
    }

    /**
     * @author: Pipi
     * @description: 查询城市下缴费类别
     * @param categoryQO:
     * @return: java.lang.String
     * @date: 2021/11/12 10:57
     **/
    public static HttpResponseModel queryCityContributionCategory(CebQueryCityContributionCategoryQO categoryQO) {
        String deviceType = categoryQO.getDeviceType();
        categoryQO.setCanal(CebBankEntity.siteCode);
        categoryQO.setDeviceType(null);
        return sendRequest(categoryQO, deviceType, CebBankConst.QUERY_CITY_CONTRIBUTION_CATEGORY);
    }
    
    /**
     * @author: Pipi
     * @description: 查询缴费类别下缴费项目
     * @param projectQO: 
     * @return: java.lang.String
     * @date: 2021/11/12 11:08
     **/
    public static HttpResponseModel queryContributionProject(CebQueryContributionProjectQO projectQO) {
        String deviceType = projectQO.getDeviceType();
        projectQO.setCanal(CebBankEntity.siteCode);
        projectQO.setDeviceType(null);
        return sendRequest(projectQO, deviceType, CebBankConst.QUERY_CONTRIBUTION_PROJECT);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费账单信息
     * @param billInfoQO:
     * @return: java.lang.String
     * @date: 2021/11/12 11:33
     **/
    public static HttpResponseModel queryBillInfo(CebQueryBillInfoQO billInfoQO) {
        String deviceType = billInfoQO.getDeviceType();
        billInfoQO.setCanal(CebBankEntity.siteCode);
        // billInfoQO.setDeviceType(null);
        return sendRequest(billInfoQO, deviceType, CebBankConst.QUERY_BILL_INFO);
    }

    /**
     * @author: Pipi
     * @description: 查询手机充值缴费账单
     * @param billQO:
     * @return: java.lang.String
     * @date: 2021/11/12 11:46
     **/
    public static HttpResponseModel queryMobileBill(CebQueryMobileBillQO billQO) {
        String deviceType = billQO.getDeviceType();
        billQO.setCanal(CebBankEntity.siteCode);
        billQO.setDeviceType(null);
        return sendRequest(billQO, deviceType, CebBankConst.QUERY_MOBILE_BILL);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录
     * @param recordQO:
     * @return: java.lang.String
     * @date: 2021/11/12 11:53
     **/
    public static HttpResponseModel queryContributionRecord(CebQueryContributionRecordQO recordQO) {
        String deviceType = recordQO.getDeviceType();
        recordQO.setCanal(CebBankEntity.siteCode);
        recordQO.setDeviceType(null);
        return sendRequest(recordQO, deviceType, CebBankConst.QUERY_CONTRIBUTION_RECORD);
    }

    /**
     * @author: Pipi
     * @description: 查询缴费记录详情
     * @param infoQO:
     * @return: java.lang.String
     * @date: 2021/11/12 14:51
     **/
    public static HttpResponseModel queryContributionRecordInfo(CebQueryContributionRecordInfoQO infoQO) {
        String deviceType = infoQO.getDeviceType();
        infoQO.setCanal(CebBankEntity.siteCode);
        infoQO.setDeviceType(null);
        return sendRequest(infoQO, deviceType, CebBankConst.QUERY_CONTRIBUTION_RECORD_INFO);
    }

    /***
     * @author: Pipi
     * @description: 创建收银台
     * @param deskQO:
     * @return: {@link HttpResponseModel}
     * @date: 2021/11/23 18:20
     **/
    public static HttpResponseModel createCashierDesk(CebCreateCashierDeskQO deskQO) {
        String deviceType = deskQO.getDeviceType();
        deskQO.setCanal(CebBankEntity.siteCode);
        deskQO.setDeviceType(null);
        return sendRequest(deskQO, deviceType, CebBankConst.CREATE_CASHIER_DESK);
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
    public static HttpResponseModel sendRequest(Object object, String deviceType, String transacCode) {
        Gson gson = new Gson();
        String reqdata_json = gson.toJson(object);
        log.info("请求object:{}", reqdata_json);
        String siteCode = CebBankEntity.siteCode;
        String version = CebBankEntity.version;
        String charset = CebBankEntity.charset;
        log.info("请求siteCode:{}", siteCode);
        log.info("请求version:{}", version);
        log.info("请求charset:{}", charset);
        log.info("请求url:{}", CebBankEntity.requestUrl);
        log.info("请求transacCode:{}", transacCode);
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
            log.info("请求sendMap:{}", sendMap.toString());
            CloseableHttpResponse httpResonse = HttpSendUtil.sendToOtherServer2(CebBankEntity.requestUrl, sendMap);
            log.info("响应接口", httpResonse);
            //接收返回，并验签
            if(null != httpResonse){
                //根据httpResonse返回结果值，进行验签
                String respStr = StringUtil.parseResponseToStr(httpResonse);
                log.info("光大云缴费响应结果:{}", respStr);
                HttpResponseModel httpResponseModel = gson.fromJson(respStr, HttpResponseModel.class);
                if (verifyhttpResonse(httpResponseModel)) {
                    return httpResponseModel;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("发生异常,返回null");
        return null;
    }

    /**
     * @author: Pipi
     * @description: 根据返回结果验签,true为验签通过,false为验签失败
     * @param httpResponseModel:
     * @return: java.lang.Boolean
     * @date: 2021/11/11 15:08
     **/
    public static Boolean verifyhttpResonse(HttpResponseModel httpResponseModel){
        String respData = httpResponseModel.getRespData();
        String respCode = httpResponseModel.getRespCode();
        String respMsg = httpResponseModel.getRespMsg();
        String signature = httpResponseModel.getSignature();
        if ("100".equals(respCode) || "2000".equals(respCode)) {
            throw new JSYException(46001, respMsg);
        }
        byte[] decodeBase64 = Base64.decodeBase64(respData);
        String respData_json = null;
        String content = new String();
        if (decodeBase64 != null) {
            respData_json = new String(decodeBase64);
            content = respCode + respMsg + respData_json;
        } else {
            content = respCode+respMsg;
        }
        log.info("返回应答，respCode={}", respCode);
        log.info("返回应答，respMsg={}", respMsg);
        log.info("返回应答，signature={}", signature);
        log.info("返回应答，respData={}", respData_json);
        //验签
        return VerifyUtil.verify(CebBankEntity.cebBankPublicKey, signature, content, "utf-8");
    }

    /***
     * @author: Pipi
     * @description: 支付回调验签
     * @param httpRequestModel:
     * @return: {@link Boolean}
     * @date: 2021/11/24 18:15
     **/
    public static Boolean verifyhttpResonse(HttpRequestModel httpRequestModel) throws IOException {
        String siteCode = httpRequestModel.getSiteCode();
        String transacCode = httpRequestModel.getTransacCode();
        String reqdata = httpRequestModel.getReqdata();
        String signature = httpRequestModel.getSignature();

        byte[] decodeBase64 = Base64.decodeBase64(httpRequestModel.getReqdata().replaceAll("%3D", ""));
        String respData_json = new String(decodeBase64);
        log.info("返回应答，respCode={}", reqdata);
        log.info("返回应答，signature={}", signature);
        log.info("返回应答，respData={}", respData_json);
        log.info("返回应答，transacCode={}", transacCode);

         String content = siteCode+"1.0.0"+transacCode+respData_json;
        //验签
        return VerifyUtil.verify(CebBankEntity.cebBankPublicKey, signature, content, "utf-8");
    }
}
