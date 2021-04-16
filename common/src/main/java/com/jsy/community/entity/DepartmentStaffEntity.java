package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @return
 * @Author lihao
 * @Description 部门成员
 * @Date 2020/11/28 10:06
 * @Param
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_department_staff")
@ApiModel(value="DepartmentStaff对象", description="")
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentStaffEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门id")
    private Long departmentId;
    
    @ApiModelProperty(value = "部门id")
    @TableField(exist = false)
    private String department;
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;

    @ApiModelProperty(value = "员工")
    private String person;

    @ApiModelProperty(value = "联系电话")
    private String phone;
    
    @ApiModelProperty(value = "职务")
    private String duty;
    
    @ApiModelProperty(value = "邮箱")
    private String email;
}
