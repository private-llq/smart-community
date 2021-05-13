package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("账单明细列表返参")
public class QueryBillInfoListVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("总条数")
    private String totalSize;

    @ApiModelProperty("本页返回数")
    private String pageSize;

    @ApiModelProperty("循环域")
    private List<QueryBillInfoVO> transList;
}
