package com.jsy.community.entity.hk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author lihao
 * @since 2021-03-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_facility_type")
@ApiModel(value="FacilityType对象", description="设备分类信息")
public class FacilityTypeEntity extends BaseEntity {

    @ApiModelProperty(value = "父id")
    @NotNull(groups = {FacilityTypeEntity.addFacilityTypeValidate.class, FacilityTypeEntity.updateFacilityTypeValidate.class},message = "父id不能为空")
    private Long pid;

    @ApiModelProperty(value = "社区id")
    @NotNull(groups = {FacilityTypeEntity.addFacilityTypeValidate.class, FacilityTypeEntity.updateFacilityTypeValidate.class},message = "社区id不能为空")
    private Long communityId;

    @ApiModelProperty(value = "类别名称")
    @NotBlank(groups = {FacilityTypeEntity.addFacilityTypeValidate.class, FacilityTypeEntity.updateFacilityTypeValidate.class},message = "类别名称不能为空")
    @Length(groups = {FacilityTypeEntity.addFacilityTypeValidate.class, FacilityTypeEntity.updateFacilityTypeValidate.class},min = 1,max = 50)
    private String name;

    public interface addFacilityTypeValidate {
    }
    
    public interface updateFacilityTypeValidate {
    }

}
