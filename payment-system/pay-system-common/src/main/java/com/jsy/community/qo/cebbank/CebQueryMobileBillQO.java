package com.jsy.community.qo.cebbank;

import lombok.Data;

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
    private String categoryType;

    // 手机号-必填
    private String mobile;
}
