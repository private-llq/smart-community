package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

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
     * 类型ID
     */
    private String typeId;
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
    private BigDecimal billAmount;
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
     * 账单状态;0:订单创建成功;1:支付成功;2:支付失败;3:销账成功;4:销账失败;5:未知状态;8:实时退款
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

    /**
     * 云缴费客户端订单总金额
     */
    private BigDecimal repoPayAmount;
    
    /**
     * 分类id
     */
    @TableField(exist = false)
    private String categoryId;
    
    /**
     * 查询时间
     */
    @TableField(exist = false)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryTime;
    
    /**
     * 户号
     */
    @TableField(exist = false)
    private String account;
    
    /**
     * 户主
     */
    @TableField(exist = false)
    private String householder;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String typeName;
    
    /**
     * 账单状态;0:订单创建成功;1:支付成功;2:支付失败;3:销账成功;4:销账失败;5:未知状态;8:实时退款
     */
    @TableField(exist = false)
    private String orderStatusName;
    
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String company;
    
    /**
     * 年-月时间
     */
    @TableField(exist = false)
    private String monthTime;

    /**
     * 类型图标
     */
    @TableField(exist = false)
    private String typePicUrl;
}
