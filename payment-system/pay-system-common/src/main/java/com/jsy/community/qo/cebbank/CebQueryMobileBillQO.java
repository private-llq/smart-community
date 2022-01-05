package com.jsy.community.qo.cebbank;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Pipi
 * @Description: 查询手机充值缴费信息
 * @Date: 2021/11/12 11:44
 * @Version: 1.0
 **/
@Data
public class CebQueryMobileBillQO extends CebBaseQO {
    // 用户标识-必填
    private String sessionId;

    // 缴费类别-必填
    @NotBlank(message = "缴费类别不能为空,同查询城市下缴费类别接口type")
    private String categoryType;

    // 手机号-必填
    @NotBlank(message = "手机号不能为空")
    private String mobile;
}
