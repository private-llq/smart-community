package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lihao
 * @since 2021-04-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_people_track")
@ApiModel(value="PeopleTrack对象", description="")
public class PeopleTrackEntity extends BaseEntity {

    @ApiModelProperty(value = "社区id")
    private Long communityId;

    @ApiModelProperty(value = "人员姓名")
    private String peopleName;

    @ApiModelProperty(value = "认证照片")
    private String authImg;

    @ApiModelProperty(value = "抓拍留影")
    private String capture;

    @ApiModelProperty(value = "认证类型 0 未认证 1 已认证")
    private Integer authType;

    @ApiModelProperty(value = "设备名称")
    private String facilityName;

    @ApiModelProperty(value = "设备编号")
    private String facilityNumber;

    @ApiModelProperty(value = "设备所在位置")
    private String facilityAddress;

}
