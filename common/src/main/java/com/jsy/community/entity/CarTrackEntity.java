package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车辆轨迹
 * </p>
 *
 * @author lihao
 * @since 2021-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_car_track")
@ApiModel(value="CarTrack对象", description="车辆轨迹")
public class CarTrackEntity extends BaseEntity {
    
    @ApiModelProperty(value = "拍摄设备id")
    private Long facilityId;
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    
    @ApiModelProperty(value = "设备所在位置")
    private String facilityAddress;
    
    @ApiModelProperty(value = "设备名称")
    private String facilityName;
    
    @ApiModelProperty(value = "设备编号")
    private String facilityNumber;

    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "车牌颜色")
    private String color;

    @ApiModelProperty(value = "认证照片")
    private String authImg;

    @ApiModelProperty(value = "车主")
    private String carOwner;

    @ApiModelProperty(value = "抓拍留影")
    private String capture;

    @ApiModelProperty(value = "认证类型 0 未认证 1 已认证")
    private Integer authType;
    
}
