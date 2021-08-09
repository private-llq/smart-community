package com.jsy.community.qo.property;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description: 物业账单入参
 * @author: Hu
 * @create: 2021-08-06 15:39
 **/
@Data
public class FinanceOrderQO implements Serializable {
    /**
     * 关联类型1房屋，2车位
     */
    private Integer associatedType;

    /**
     * 房屋或者车位id
     */
    private Long targetId;

    /**
     * 缴费项目名称模糊查询
     */
    private Long feeRuleName;

    /**
     * 收费单号
     */
    private String orderNum;

    /**
     * 生成时间
     */
    private LocalDate orderTime;

    /**
     * 账单开始时间
     */
    private LocalDate beginTime;
    /**
     * 账单结束时间
     */
    private LocalDate overTime;

    /**
     * 状态1显示，2隐藏
     */
    private Integer hid;
}
