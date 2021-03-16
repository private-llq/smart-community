package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author qq459799974
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "HouseMember对象", description = "房间成员表")
@TableName("t_house_member")
public class HouseMemberEntity implements Serializable {

    @TableId
    @ApiModelProperty(value = "家属ID")
    private Long id;

    @TableLogic
    @ApiModelProperty(hidden = true)
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @ApiModelProperty(value = "房间ID")
    private Long houseId;

    @ApiModelProperty(value = "房主ID")
    private String householderId;

    @ApiModelProperty(value = "家属名称")
    private String name;
    @ApiModelProperty(value = "家属性别")
    private Integer sex;
    @ApiModelProperty(value = "家属电话")
    private String mobile;
    
    @ApiModelProperty(value = "证件类型1.身份证 2.护照",required = true)
    private Integer identificationType;
    @ApiModelProperty(value = "家属身份证号码/护照号码")
    private String idCard;
    
    @ApiModelProperty(value = "与业主关系 1.夫妻 2.父子 3.母子 4.父女 5.母女 6.亲属")
    private Integer relation;

    @ApiModelProperty(value = "0，其他，1亲属，2租客")
    private Integer personType;

    @ApiModelProperty(value = "租期结束时间")
    private LocalDate leaseOverTime;

    @ApiModelProperty(value = "租期开始时间")
    private LocalDate leaseStartTime;

}
