package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class DepartmentStaffEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门id")
    @NotNull(groups = {DepartmentStaffEntity.addStaffValidate.class, DepartmentStaffEntity.updateStaffValidate.class}, message = "部门id不能为空")
    private Long departmentId;

    @ApiModelProperty(value = "联系人")
    @NotBlank(groups = {DepartmentStaffEntity.addStaffValidate.class, DepartmentStaffEntity.updateStaffValidate.class},message = "联系人不能为空")
    private String person;

    @ApiModelProperty(value = "联系电话")
    @NotBlank(groups = {DepartmentStaffEntity.addStaffValidate.class, DepartmentStaffEntity.updateStaffValidate.class},message = "联系电话不能为空")
    private String phone;
    
    public interface addStaffValidate {
    }
    
    public interface updateStaffValidate {
    }
}
