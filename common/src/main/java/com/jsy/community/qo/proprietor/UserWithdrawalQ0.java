package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户账户提现相关
 **/
@Data
public class UserWithdrawalQ0 implements Serializable {

    @ApiModelProperty(value = "提现金额")
    @Range(min = 0,message = "提现金额错误")
    @NotNull(message = "请确定提现金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付密码")
    @NotBlank(message = "支付密码不能为空")
    private String payPassword;

}
