package com.jsy.community.qo.property;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description: 物业财务账单操作接收类
 * @author: Hu
 * @create: 2021-08-09 13:49
 **/
@Data
public class FinanceOrderOperationQO implements Serializable {

    /**
     * 批次开始时间
     */
    private LocalDate orderTimeBegin;
    /**
     * 批次结束时间
     */
    private LocalDate orderTimeOver;
    /**
     * 账单开始时间
     */
    private LocalDate beginTime;
    /**
     * 账单结束时间
     */
    private LocalDate overTime;
    /**
     * 物业类型
     */
    private Long type;
}
