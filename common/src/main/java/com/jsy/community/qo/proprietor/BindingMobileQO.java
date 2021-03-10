package com.jsy.community.qo.proprietor;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-08 10:55
 **/
@Data
@ApiModel("微信绑定手机")
public class BindingMobileQO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "电话")
    @NotNull(groups = {BindingMobileValidated.class},message = "手机号不能为空！")
    @NotBlank(groups = {BindingMobileValidated.class},message = "手机号不能为空！")
    @Pattern(regexp = RegexUtils.REGEX_MOBILE,groups = {BindingMobileValidated.class},message = "手机号不正确，请传入正确的手机号！")
    private String mobile;
    @ApiModelProperty(value = "验证码")
    private String code;


    public interface BindingMobileValidated{}
}
