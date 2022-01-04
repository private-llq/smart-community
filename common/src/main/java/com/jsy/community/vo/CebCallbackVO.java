package com.jsy.community.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/24 9:39
 * @Version: 1.0
 **/
@Data
public class CebCallbackVO implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate merOrderDate;
    private String merOrderNo;
//    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String orderDate;
    private String transacNo;
    private Integer order_status;
    private BigDecimal payAmount;
    private Integer payType;
    private String errorCode;
    private String errorMessage;
}
