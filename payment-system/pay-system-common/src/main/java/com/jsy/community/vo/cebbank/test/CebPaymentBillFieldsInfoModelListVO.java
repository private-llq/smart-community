package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/23 19:40
 * @Version: 1.0
 **/
@Data
public class CebPaymentBillFieldsInfoModelListVO implements Serializable {
    private String fieldInfoId;
    private String paymentItemId;
    private String fieldInfo;
}
