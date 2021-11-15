package com.jsy.community.qo.unionpay;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/11 11:12
 * @Version: 1.0
 **/
public class CebBankPostModel {
    //渠道标识
    private String siteCode;
    //版本号
    private String version;
    //终端设备类型，1-PC个人电脑 2-手机终端
    private String deviceType;
    //交易名
    private String transacCode;
    //请求数据报文，json格式，采用base64编码，具体在接口说明中详述
    private String reqdata;
    //数字签名，采用Base64编码和MD5withRSA算法实现，签名内容顺序为：siteCode、version、transacCode和reqdata_json(reqdata，base64前的json串)
    private String signature;
    //reqdata编码字符集，数字签名和base64编码中使用的字符集，仅支持 utf-8
    private String charset;

    public String getSiteCode() {
        return siteCode;
    }
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getTransacCode() {
        return transacCode;
    }
    public void setTransacCode(String transacCode) {
        this.transacCode = transacCode;
    }
    public String getReqdata() {
        return reqdata;
    }
    public void setReqdata(String reqdata) {
        this.reqdata = reqdata;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getCharset() {
        return charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
}
