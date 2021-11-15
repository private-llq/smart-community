package com.jsy.community.vo.unionpay;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 交易明细列表返参
 * @Date: 2021/5/12 10:00
 * @Version: 1.0
 **/
@Data
public class UnionPayTransListVO extends UnionPayBaseVO implements Serializable {

    /**
     * 页面大小
     */
    private String pageSize;

    /**
     * 页数（第几页）
     */
    private String pageNo;

    /**
     * 记录总数
     */
    private String total;

    /**
     * 循环域(S)
     */
    private List<UnionPayTransVO> rowList;
}
