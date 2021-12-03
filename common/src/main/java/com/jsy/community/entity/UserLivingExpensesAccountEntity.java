package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private String groupId;
    /**
     * 户号
     */
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
    private String companyId;
    /**
     * 公司名称
     */
    private String company;
    /**
     * 分类ID
     */
    private String categoryId;
    /**
     * 分类名称
     */
    private String category;
    /**
     * 项目ID
     */
    private String itemId;
    /**
     * 项目code
     */
    private String itemCode;
}
