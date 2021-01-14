package com.jsy.community.vo.shop;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 生活缴费月份账单
 * @author: Hu
 * @create: 2021-01-14 17:11
 **/
@Data
public class PaymentRecordsMapVO implements Serializable {
    private Map map;
}
