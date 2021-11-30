package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询的账单结果VO
 * @Date: 2021/11/13 10:20
 * @Version: 1.0
 **/
@Data
public class CebBillQueryResultModelVO implements Serializable {
    // 缴费号码
    private String billKey;

    // 预留字段
    private String item1;

    // 预留字段
    private String item2;

    // 预留字段
    private String item3;

    // 预留字段
    private String item4;

    // 预留字段
    private String item5;

    // 预留字段
    private String item6;

    // 预留字段
    private String item7;

    private List<CebBillQueryResultDataModelVO> billQueryResultDataModelList;
}
