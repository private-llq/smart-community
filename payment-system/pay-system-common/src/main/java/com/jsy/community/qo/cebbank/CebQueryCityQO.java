package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 查询城市的接口QO
 * @Date: 2021/11/12 10:19
 * @Version: 1.0
 **/
@Data
public class CebQueryCityQO extends CebBaseQO {
    // 用户标识-必填
    private String sessionId;
    // 服务城市范围-非必填
    // “ALL”查询全部服务城市
    // “SOME”只查询服务的城市
    private String cityRange;
}
