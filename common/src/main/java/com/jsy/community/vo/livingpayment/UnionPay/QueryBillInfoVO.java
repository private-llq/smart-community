package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 账单明细返参
 * @Date: 2021/5/12 11:24
 * @Version: 1.0
 **/
@Data
@ApiModel("账单明细返参")
public class QueryBillInfoVO implements Serializable {

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("商户订单号")
    private String mctOrderNo;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("账户外部编号")
    private String acctExtNo;

    @ApiModelProperty("账户名称")
    private String acctName;

    @ApiModelProperty("交易代码")
    private String transCode;

    @ApiModelProperty("交易名称")
    private String transName;

    @ApiModelProperty("收支类型")
    private String loanMark;

    @ApiModelProperty("交易金额")
    private String transAmt;

    @ApiModelProperty("交易后余额")
    private String postBal;

    @ApiModelProperty("清算日期")
    private String settDate;

    @ApiModelProperty("交易时间")
    private String transTime;

    @ApiModelProperty("对方账号")
    private String otherAcctNo;

    @ApiModelProperty("对方账户外部编号")
    private String otherAcctExtNo;

    @ApiModelProperty("对方账户名")
    private String otherAcctName;

    @ApiModelProperty("渠道商户号")
    private String chnlMerNo;

    @ApiModelProperty("渠道商户名称")
    private String chnlMerName;

    @ApiModelProperty("渠道终端号")
    private String chnlTermNo;

    @ApiModelProperty("资金渠道")
    private String amtChnl;

    @ApiModelProperty("资金渠道名称")
    private String amtChnlName;

    @ApiModelProperty("业务类型编号")
    private String bizType;

    @ApiModelProperty("业务类型名称")
    private String bizTypeName;

    @ApiModelProperty("业务商户号")
    private String bizMerNo;

    @ApiModelProperty("摘要")
    private String abst;

    @ApiModelProperty("交易备注")
    private String remark;

    @ApiModelProperty("备用域1")
    private String def1;

    @ApiModelProperty("备用域2")
    private String def2;

    @ApiModelProperty("备用域3")
    private String def3;

    @ApiModelProperty("备用域4")
    private String def4;

}
