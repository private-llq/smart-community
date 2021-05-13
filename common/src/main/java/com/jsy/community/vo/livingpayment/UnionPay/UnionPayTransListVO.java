package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("交易明细列表返参")
public class UnionPayTransListVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("页面大小")
    private String pageSize;

    @ApiModelProperty("页数（第几页）")
    private String pageNo;

    @ApiModelProperty("记录总数")
    private String total;

    @ApiModelProperty("循环域(S)")
    private List<UnionPayTransVO> rowList;
}
