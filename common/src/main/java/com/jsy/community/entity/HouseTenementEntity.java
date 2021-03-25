package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author qq459799974
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "HouseMember对象", description = "房间成员表")
@TableName("t_house_tenement")
public class HouseTenementEntity extends BaseEntity {
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
    @ApiModelProperty(value = "证件类型1.身份证 2.护照", required = true)
    private Integer identificationType;
    @ApiModelProperty(value = "家属身份证号码/护照号码")
    private String idCard;
    @ApiModelProperty(value = "租期结束时间")
    private LocalDateTime leaseOverTime;
    @ApiModelProperty(value = "租期开始时间")
    private LocalDateTime leaseStartTime;

}
