package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费户号表实体
 * @Date: 2021/12/2 16:36
 * @Version: 1.0
 **/
@Data
@TableName("t_user_living_expenses_account")
public class UserLivingExpensesAccountEntity extends BaseEntity {
    /**
     * 用户uid
     */
    private String uid;
    /**
     * 分组ID
     */
    @NotBlank(message = "分组ID不能为空")
    private String groupId;
    /**
     * 省份ID
     */
    @NotBlank(message = "省份ID不能为空")
    private String provinceId;
    /**
     * 城市ID
     */
    @NotBlank(message = "城市ID不能为空")
    private String cityId;
    /**
     * 城市code
     */
    @NotBlank(message = "城市code不能为空")
    private String cityCode;
    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空")
    private String cityName;
    /**
     * 户号
     */
    @NotBlank(message = "户号不能为空")
    @Length(min = 6, message = "户号不能小于6位")
    private String account;
    /**
     * 户主
     */
    private String householder;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 公司ID
     */
    @NotBlank(message = "公司ID不能为空")
    private String companyId;
    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    private String company;
    /**
     * 缴费类型ID
     */
    @NotBlank(message = "缴费类型ID不能为空")
    private String typeId;
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String typeName;
    /**
     * 分类ID
     */
    @NotBlank(message = "分类ID不能为空")
    private String categoryId;
    /**
     * 项目ID
     */
    @NotBlank(message = "项目id不能为空")
    private String itemId;
    /**
     * 项目code
     */
    @NotBlank(message = "项目code不能为空")
    private String itemCode;
    /**
     * 业务流程
     * 0：先查后缴1：直接缴费2：二次查询
     */
    @TableField(exist = false)
    @NotNull(message = "业务流程不能为空")
    private Integer businessFlow;

    /**
     * 1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序-部分接口必填
     */
    @NotBlank(message = "终端类型不能为空;1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序")
    @TableField(exist = false)
    private String deviceType;

    /**
     * 用户手机号
     */
    @TableField(exist = false)
    private String mobile;
}
