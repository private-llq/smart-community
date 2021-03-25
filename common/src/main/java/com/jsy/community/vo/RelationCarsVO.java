package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("返回车辆信息")
public class RelationCarsVO extends BaseVO {
    @ApiModelProperty("车牌号")
    private String carPlate;// 85
    @ApiModelProperty("车辆类型")
    private Integer carType;
    @ApiModelProperty("车辆类型名称")
    private String carTypeText;
    @ApiModelProperty("行驶证图片")
    private String drivingLicenseUrl;

    //手机号
    @ApiModelProperty(hidden = true)
    private String phoneTel;

    //用户ID
    @ApiModelProperty(hidden = true)
    private String uid;

    //家属名字
    @ApiModelProperty(hidden = true)
    private String owner;

    //所属社区
    @ApiModelProperty(hidden = true)
    private Long communityId;

}
