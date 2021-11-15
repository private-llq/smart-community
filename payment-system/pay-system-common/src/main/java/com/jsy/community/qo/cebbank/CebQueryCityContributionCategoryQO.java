package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 查询城市下缴费类别QO
 * @Date: 2021/11/12 10:48
 * @Version: 1.0
 **/
@Data
public class CebQueryCityContributionCategoryQO extends CebBaseQO {
    // 城市名称-必填
    private String cityName;
    // 用户标识-必填
    private String sessionId;
}
