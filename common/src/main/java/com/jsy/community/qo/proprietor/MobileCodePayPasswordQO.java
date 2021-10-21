package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class MobileCodePayPasswordQO implements Serializable {
    @ApiModelProperty("支付密码")
    @NotEmpty(groups = AddPasswordQO.payPasswordVGroup.class, message = "支付密码不能为空")
    @Length(groups = AddPasswordQO.payPasswordVGroup.class, min = 6, max = 6, message = "支付密码长度为6位")
    private String payPassword;

    @ApiModelProperty("确认支付密码")
    @NotEmpty(groups = AddPasswordQO.payPasswordVGroup.class, message = "确认支付密码不能为空")
    @Length(groups = AddPasswordQO.payPasswordVGroup.class, min = 6, max = 6, message = "确认支付密码长度为6位")
    private String confirmPayPassword;

    @ApiModelProperty("验证码")
    @NotEmpty(groups = AddPasswordQO.passwordVGroup.class, message = "验证码不能为空")
    @Length(groups = AddPasswordQO.payPasswordVGroup.class, min = 4, max = 4, message = "验证码长度为4")
    private String code;
}
