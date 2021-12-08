package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费账单表实体
 * @Date: 2021/12/2 17:00
 * @Version: 1.0
 **/
@Data
@TableName("t_user_living_expenses_bill")
public class UserLivingExpensesBillEntity extends BaseEntity {
    /**
     * 项目ID
     */
    private String itemId;
    /**
     * 项目code
     */
    private String itemCode;
    /**
     * 户号
     */
    private String billKey;
    /**
     * 账单金额(分)
     */
    private String billAmount;
    /**
     * 账单交易码
     */
    private String queryAcqSsn;
    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 合同编号
     */
    private String contactNo;
    /**
     * 余额
     */
    private String balance;
    /**
     * 起始日期
     */
    private String beginDate;
    /**
     * 截止日期
     */
    private String endDate;
    /**
     * 备用字段1
     */
    private String fieldA;
    /**
     * 备用字段2
     */
    private String fieldB;
    /**
     * 备用字段3
     */
    private String fieldC;
    /**
     * 备用字段4
     */
    private String fieldD;
    /**
     * 备用字段5
     */
    private String fieldE;
    /**
     * 账单状态;0:未缴;1:已缴
     */
    private Integer billStatus;

    private String rangLimit;
    private String chooseAmount;
}
