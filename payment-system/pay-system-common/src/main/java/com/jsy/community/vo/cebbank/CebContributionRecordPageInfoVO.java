package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/13 14:59
 * @Version: 1.0
 **/
@Data
public class CebContributionRecordPageInfoVO implements Serializable {
    // 每页条数
    private Integer pageSize;

    // 总页数
    private Integer totalPage;

    // 当前页
    private Integer currentPage;

    // 当前记录
    private Integer currentRec;

    // 下一记录
    private Integer nextRec;

    // 下一页
    private Integer nextPage;

    // 上一页
    private Integer prePage;
}
