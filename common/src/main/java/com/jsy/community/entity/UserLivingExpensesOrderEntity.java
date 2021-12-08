package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费订单表实体
 * @Date: 2021/12/2 17:44
 * @Version: 1.0
 **/
@Data
@TableName("t_user_living_expenses_order")
public class UserLivingExpensesOrderEntity extends BaseEntity {
    /**
     * 用户uid
     */
    private String uid;
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
     * 账单id
     */
    private String billId;
    /**
     * 账单金额(分)
     */
    private String billAmount;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 合同编号
     */
    private String contactNo;
    /**
     * 完整参数字符串
     */
    private String fullParam;
    /**
     * 账单状态;0:订单创建成;1:支付成功;2:支付失败;3:销账成功;4:销账失败;5:未知状态;8:实时退款
     */
    private Integer orderStatus;
    /**
     * 支付方式:1:银联支付;2:微信支付;3:支付宝支付;4:中金支付
     */
    private Integer payType;
    /**
     * 受理日期
     */
    private String orderDate;
    /**
     * 受理流水号
     */
    private String transacNo;
}
