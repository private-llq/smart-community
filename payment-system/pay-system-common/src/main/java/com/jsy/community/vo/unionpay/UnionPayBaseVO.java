package com.jsy.community.vo.unionpay;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 银联支付基础响应接参
 * @Date: 2021/4/12 15:17
 * @Version: 1.0
 **/
@Data
public class UnionPayBaseVO  {
    // 应答码
    private String rspCode;

    // 应答描述
    private String rspResult;
}
