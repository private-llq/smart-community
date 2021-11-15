package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 查询缴费类别下缴费项目QO
 * @Date: 2021/11/12 10:59
 * @Version: 1.0
 **/
@Data
public class CebQueryContributionProjectQO extends CebBaseQO {
    // 用户标识-必填
    private String sessionId;
    // 城市名称-必填
    private String cityName;
    // 缴费类别(由接口查询)-必填
    private String type;
}
