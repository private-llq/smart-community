package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/23 19:41
 * @Version: 1.0
 **/
@Data
public class CebQueryPaymentBillParamModelListVO implements Serializable {
    private String selectParamId;
    private String paymentItemId;
    private String priorLevel;
    private String name;
    private String minFieldLength;
    private String maxFieldLength;
    private String listBoxOptions;
    private String type;
    private String description;
    private String filedNum;
    private String isNull;
    private String filedType;
    private String isScan;
    private String inputType;
    private String keyboardType;
    private String showLevel;
}
