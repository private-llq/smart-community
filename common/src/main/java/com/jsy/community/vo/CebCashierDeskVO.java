package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 光大收银台VO
 * @Date: 2021/12/30 14:55
 * @Version: 1.0
 **/
@Data
public class CebCashierDeskVO implements Serializable {
    private String code;
    private String url;
    private String message;
    /**
     * 本系统订单号
     */
    private String orderNo;
}
