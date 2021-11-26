package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/23 18:20
 * @Version: 1.0
 **/
@Data
public class CebCashierDeskVO implements Serializable {
    private String code;
    private String url;
    private String message;
}
