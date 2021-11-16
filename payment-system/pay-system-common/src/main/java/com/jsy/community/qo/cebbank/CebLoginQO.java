package com.jsy.community.qo.cebbank;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费注册QO
 * @Date: 2021/11/12 10:08
 * @Version: 1.0
 **/
@Data
public class CebLoginQO extends CebBaseQO {
    // 用户手机号-必填
    @NotBlank(message = "用户手机号不能为空")
    private String userPhone;
}
