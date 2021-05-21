package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 账单明细列表返参
 * @Date: 2021/5/12 11:22
 * @Version: 1.0
 **/
@Data
public class QueryBillInfoListVO extends UnionPayBaseVO implements Serializable {

    /**
     * 总条数
     */
    private String totalSize;

    /**
     * 本页返回数
     */
    private String pageSize;

    /**
     * 循环域
     */
    private List<QueryBillInfoVO> transList;
}
