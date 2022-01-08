package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费类型表实体
 * @Date: 2022/1/5 15:31
 * @Version: 1.0
 **/
@Data
@TableName("t_user_living_expenses_type")
public class LivingExpensesTypeEntity extends BaseEntity {
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 图标地址
     */
    private String picUrl;
    /**
     * 排序
     */
    private String sort;
    /**
     * 版本号
     */
    private String version;
}
