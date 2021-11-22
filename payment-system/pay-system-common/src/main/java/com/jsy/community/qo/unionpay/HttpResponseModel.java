package com.jsy.community.qo.unionpay;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/11 11:51
 * @Version: 1.0
 **/
public class HttpResponseModel {
    private String respData;
    private String respCode;
    private String respMsg;
    private String signature;

    public String getRespData() {
        return respData;
    }
    public void setRespData(String respData) {
        this.respData = respData;
    }
    public String getRespCode() {
        return respCode;
    }
    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }
    public String getRespMsg() {
        return respMsg;
    }
    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
}
