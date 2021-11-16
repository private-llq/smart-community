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
    // 合同号
    private String contractNo;

    // 客户名称
    private String customerName;

    // 余额
    private String balance;

    // 开始时间
    private String beginDate;

    // 结束时间
    private String endDate;

    // 备用字段
    private String filed1;

    // 备用字段
    private String filed2;

    // 备用字段
    private String filed3;

    // 备用字段
    private String filed4;

    // 备用字段
    private String filed5;
}
