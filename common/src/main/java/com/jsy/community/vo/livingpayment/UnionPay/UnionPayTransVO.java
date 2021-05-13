package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 交易明细返参
 * @Date: 2021/5/12 10:04
 * @Version: 1.0
 **/
@Data
@ApiModel("交易明细返参")
public class UnionPayTransVO implements Serializable {

    @ApiModelProperty("交易订单号")
    private String transOrderNo;

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("对方钱包ID")
    private String otherWalletId;

    @ApiModelProperty("对方钱包名称")
    private String otherWalletName;

    @ApiModelProperty("共管子账号")
    private String coadminAcctNo;

    @ApiModelProperty("共管子账号名称")
    private String coadminAcctName;

    @ApiModelProperty("交易日期")
    private String transDate;

    @ApiModelProperty("交易时间")
    private String transTime;

    @ApiModelProperty("交易类型编码")
    private String transType;

    @ApiModelProperty("交易类型名称")
    private String transTypeName;

    @ApiModelProperty("商户号")
    private String merNo;

    @ApiModelProperty("商户简称")
    private String merName;

    @ApiModelProperty("交易金额")
    private String transAmt;

    @ApiModelProperty("共管金额")
    private String coadminAmt;

    @ApiModelProperty("可用金额")
    private String avlblAmt;

    @ApiModelProperty("清算金额")
    private String settAmt;

    @ApiModelProperty("清算日期")
    private String settDate;

    @ApiModelProperty("处理状态")
    private String procStatus;

    @ApiModelProperty("状态描述")
    private String procStatusDscrb;

    @ApiModelProperty("处理结果描述")
    private String procResultDscrb;

    @ApiModelProperty("调整标记")
    private String adjustFlag;

    @ApiModelProperty("商户订单号")
    private String mctOrderNo;

    @ApiModelProperty("银行行号")
    private String bankCode;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行卡号")
    private String bankAcctNo;

    @ApiModelProperty("银行户名")
    private String bankAcctName;

    @ApiModelProperty("批次号")
    private String regId;

    @ApiModelProperty("批次明细号")
    private String regDtlSn;

    @ApiModelProperty("摘要")
    private String abst;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("交易手续费")
    private String transFee;

    @ApiModelProperty("交易手续费入账钱包id")
    private String transFeeWalletId;

    @ApiModelProperty("交易手续费入账钱包名称")
    private String transFeeWalletName;

    @ApiModelProperty("资金符号")
    private String amtSymbol;

    @ApiModelProperty("资金计划项目ID")
    private String cptlPlnPrjctId;

    @ApiModelProperty("资金计划项目编号")
    private String cptlPlnPrjctCode;

    @ApiModelProperty("资金计划项目名称")
    private String cptlPlnPrjctName;

    @ApiModelProperty("付款类型")
    private String payType;

    @ApiModelProperty("付款银行行号")
    private String payBankCode;

    @ApiModelProperty("付款银行名称")
    private String payBankName;

    @ApiModelProperty("付款银行卡号")
    private String payBankAcctNo;

    @ApiModelProperty("付款银行账户名称")
    private String payBankAcctName;
}
