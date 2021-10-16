package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WithdrawalResulrVO implements Serializable {
    @ApiModelProperty("操作状态码")
    private String code;
    @ApiModelProperty("返回详情")
    private String msg;
    @ApiModelProperty("操作状态")
    private Boolean success;
    @ApiModelProperty("错误详情码，当success为true时，该属性为空。")
    private String subCode;

    public WithdrawalResulrVO() {
    }

    public WithdrawalResulrVO(String code, String msg, Boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    public WithdrawalResulrVO(String code, String msg, Boolean success, String subCode) {
        this.code = code;
        this.msg = msg;
        this.success = success;
        this.subCode = subCode;
    }
}
