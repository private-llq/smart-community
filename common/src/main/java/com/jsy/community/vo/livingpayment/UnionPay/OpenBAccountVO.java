package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联开B端账户返参
 * @Date: 2021/4/16 17:00
 * @Version: 1.0
 **/
@Data
public class OpenBAccountVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("登记ID")
    private String regId;

    @ApiModelProperty("随机校验码")
    private String randomValidCode;

    @ApiModelProperty("上传目录路径")
    private String uploadFolderPath;

    @ApiModelProperty("确认金金额")
    private String confirmAmt;

    @ApiModelProperty("确认金收款方开户行行名")
    private String bankName;

    @ApiModelProperty("确认金收款方电子联行号")
    private String elecBankNo;

    @ApiModelProperty("确认金收款方支行名称")
    private String elecBankName;

    @ApiModelProperty("确认金收款方账户名称")
    private String bankAcctName;

    @ApiModelProperty("确认金收款方账号")
    private String bankAcctNo;
}
