package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 查询缴费记录QO
 * @Date: 2021/11/12 11:48
 * @Version: 1.0
 **/
@Data
public class CebQueryContributionRecordQO extends CebBaseQO {
    // 用户标识
    private String sessionId;

    // 缴费状态
    // 0：处理中;1：成功;2：失败;-1：全部
    private String status;

    // 查询开始时间
    // 格式：yyyy-mm-dd
    private String startDate;

    // 查询结束时间
    // 格式：yyyy-mm-dd
    private String endDate;

    // 分页大小;默认10
    private String pageSize;

    // 当前查询页数;默认1
    private String curPage;
}
