package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 银联交易记录表(包含转账, 提现)
 * @Date: 2021/5/10 11:41
 * @Version: 1.0
 **/
@Data
@TableName("t_union_pay_trade_record")
@ApiModel("银联交易记录表(包含转账, 提现)")
public class UnionPayTradeRecordEntity extends BaseEntity {

    @ApiModelProperty("用户ID")
    private String uid;

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("转账金额,单位元")
    private BigDecimal amount;

    @ApiModelProperty("手续费")
    private BigDecimal feeAmt;

    @ApiModelProperty("手续费收入钱包ID")
    private String feeIntoWalletId;

    @ApiModelProperty("提现银行账号")
    private String bankAcctNo;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("摘要")
    private String abst;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("交易状态,1提交,2成功")
    private Integer tradeStatue;



















}
