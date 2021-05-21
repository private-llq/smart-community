package com.jsy.community.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: Pipi
 * @Description: 银联钱包关联银行卡表
 * @Date: 2021/4/10 13:35
 * @Version: 1.0
 **/
@ApiModel("银联钱包关联银行卡表")
@Data
@TableName("t_user_union_pay_wallet_bank")
public class UnionPayWalletBankEntity {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "银联钱包id")
    private String walletId;

    @ApiModelProperty(value = "银行卡号")
    private String bankAcctNo;

    @ApiModelProperty(value = "开户行")
    private String accountBank;

    @ApiModelProperty(value = "十二位电子联行号")
    private String bankId;

    @ApiModelProperty(value = "三位行号")
    private String threeBankNo;

    @ApiModelProperty(value = "是否默认,0:不是,1:是")
    private Integer isDefault;

    @ApiModelProperty(value = "删除标记,0:未删除,1:已删除")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
