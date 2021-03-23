package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 家属和租户汽车实体类
 * @author: Hu
 * @create: 2021-03-23 13:33
 **/
@Data
@TableName("t_relation_car")
public class RelationCarEntity extends BaseEntity {
    @ApiModelProperty(value = "用户uid")
    private String uid;
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    @ApiModelProperty(value = "车位ID")
    private Long carPositionId;
    @ApiModelProperty(value = "车牌号")
    private String carPlate;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty(value = "车主")
    private String owner;
    @ApiModelProperty(value = "身份证")
    private String idCard;
    @ApiModelProperty(value = "来访车辆类型 1.微型车 2.小型车 3.紧凑型车 4.中型车 5.中大型车 6.其他车辆类型")
    private Integer carType;
    @ApiModelProperty(value = "1家属2租户")
    private Integer relationType;
    @ApiModelProperty(value = "家属或者租户id")
    private Long relationshipId;
    @ApiModelProperty(value = "行驶证图片")
    private String drivingLicenseUrl;
    @ApiModelProperty(value = "是否过审")
    private Integer checkStatus;
    @ApiModelProperty(value = "过审时间")
    private LocalDateTime checkTime;
}
