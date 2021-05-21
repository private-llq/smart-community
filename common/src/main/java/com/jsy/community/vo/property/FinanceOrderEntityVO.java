package com.jsy.community.vo.property;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 啥也不是
 * @Date: 2021/4/27 11:40
 * @Version: 1.0
 **/
@Data
public class FinanceOrderEntityVO implements Serializable {

    private String createTime;

    private BigDecimal totalMoney;

    // 支付状态
    private Integer orderStatus;

    // 账单类型
    private String orderType;
}
