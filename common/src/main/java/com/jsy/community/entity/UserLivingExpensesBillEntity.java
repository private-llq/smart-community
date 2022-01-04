package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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
    @NotBlank(groups = {AddOrderValidateGroup.class}, message = "项目ID不能为空")
    private String itemId;
    /**
     * 项目code
     */
    @NotBlank(groups = {AddOrderValidateGroup.class}, message = "项目code不能为空")
    private String itemCode;
    /**
     * 户号
     */
    @NotBlank(groups = {QueryBillValidateGroup.class, AddOrderValidateGroup.class}, message = "缴费户号不能为空")
    private String billKey;
    /**
     * 用户uid
     */
    private String uid;
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

    /**
     * 支付金额
     */
    @NotNull(groups = {AddOrderValidateGroup.class}, message = "支付金额不能为空")
    @TableField(exist = false)
    private BigDecimal payAmount;

    /**
     * 终端类型
     */
    @NotBlank(groups = {AddOrderValidateGroup.class}, message = "终端类型不能为空;1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序")
    @TableField(exist = false)
    private String deviceType;

    // 手机充值标记-必填
    // 手机充值该字段必传1。手机充值时可以不用传filed,qryAcnSsn,contractNo、这些5接口没有返回的值
    @TableField(exist = false)
    private String type;

    /**
     * 查询账单验证组
     */
    public interface QueryBillValidateGroup{}

    /**
     * 添加订单验证组
     */
    public interface AddOrderValidateGroup{}
}
