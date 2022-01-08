package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/24 17:33
 * @Version: 1.0
 **/
@Data
public class HttpRequestModel implements Serializable {
     private String transacCode;
     private String deviceType;
     private String charset;
     private String siteCode;
     private String signature;
     private String reqdata;
     private String version;
}
