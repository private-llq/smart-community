package com.jsy.community.qo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 创建收银台的嵌套QO
 * @Date: 2021/11/13 15:18
 * @Version: 1.0
 **/
@Data
public class CebBillQueryResultDataModelQO implements Serializable {
    private String contractNo;
    private String customerName;
    private String originalCustomerName;
    private String balance;
    private String payAmount;
    private String beginDate;
    private String endDate;
    private String filed1;
    private String filed2;
    private String filed3;
    private String filed4;
    private String filed5;
    private String payBeginDate;
    private String payEndDate;
    private String serialNumber;
    private String account;
}
