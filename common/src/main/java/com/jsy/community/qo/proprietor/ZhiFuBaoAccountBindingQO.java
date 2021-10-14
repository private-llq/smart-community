package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class ZhiFuBaoAccountBindingQO implements Serializable {

    @ApiModelProperty(value = "支付宝账号（支持手机号码和邮箱）")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty(value = "支付宝账号真实姓名")
    @NotBlank(message = "姓名不能为空")
    private String realname;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
