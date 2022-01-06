package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 用户生活缴费分组表实体
 * @Date: 2021/12/2 16:25
 * @Version: 1.0
 **/
@Data
@TableName("t_user_living_expenses_group")
public class UserLivingExpensesGroupEntity extends BaseEntity {
    /**
     * 用户uid
     */
    private String uid;
    /**
     * 分组名称
     */
    @NotBlank(message = "分组名称不能为空")
    @Length(min = 2, max = 16, message = "分组名称长度为2到16个字符")
    private String groupName;

    /**
     * 默认分组;0不是,1是
     */
    private Integer isDefault;

    /**
     * 户号列表
     */
    @TableField(exist = false)
    private List<UserLivingExpensesAccountEntity> accountEntityList;
}
