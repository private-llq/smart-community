package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 户主详情表
 * </p>
 *
 * @author lihao
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pay_user_details")
@ApiModel(value="PayUserDetails对象", description="户主详情表")
public class PayUserDetailsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "户主姓名")
    private String name;

    @ApiModelProperty(value = "身份证号")
    @TableField("idCard")
    private String idCard;

    @ApiModelProperty(value = "家庭住址")
    private String address;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "性别 0 男 1 女")
    private Integer sex;
}
