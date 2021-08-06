package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author DKS
 * @since 2021-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_house_building_type")
public class HouseBuildingTypeEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @ApiModelProperty(value = "楼宇分类名称")
    @NotNull(groups = {addHouseBuildingTypeGroup.class}, message = "缺少楼宇分类名称")
    private String propertyTypeName;
    
    /**
     * 新增楼宇分类验证组
     */
    public interface addHouseBuildingTypeGroup{}
}