package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author lihao
 * @since 2021-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_organization")
@ApiModel(value="Organization对象", description="组织机构对象实体")
public class OrganizationEntity extends BaseEntity {

    @ApiModelProperty(value = "父id")
    @NotNull(groups = {addOrganizationValidate.class, updateOrganizationValidate.class}, message = "父id不能为空")
    private Long pid;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "数据库没有该字段")
    private String parentName;

    @ApiModelProperty(value = "社区id")
    @NotNull(groups = {addOrganizationValidate.class, updateOrganizationValidate.class}, message = "社区id不能为空")
    private Long communityId;

    @ApiModelProperty(value = "组织名称")
    @NotNull(groups = {addOrganizationValidate.class, updateOrganizationValidate.class}, message = "组织机构名称不能为空")
    private String name;

    @ApiModelProperty(value = "排序序号 0-99")
    private Integer sort;

    @ApiModelProperty(value = "是否为维修部 0 否 1 是")
    private Integer isRepair;
    
    
    public interface addOrganizationValidate {
    }
    
    public interface updateOrganizationValidate {
    }

}
