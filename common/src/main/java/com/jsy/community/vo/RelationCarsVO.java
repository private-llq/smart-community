package com.jsy.community.vo;

import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("放回车辆信息")
public class RelationCarsVO extends BaseEntity {
    @ApiModelProperty("车牌号")
    private String carId;// 85
    @ApiModelProperty("车辆类型")
    private Integer carType;
    @ApiModelProperty("车辆类型名称")
    private String carTypeName;
    @ApiModelProperty("车辆图片")
    private String carImgURL;

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

    //车辆位置
    @ApiModelProperty("车辆位置")
    private Long carPosition;

    //是否通过审核
    @ApiModelProperty("是否通过审核")
    private Integer checkStatus;

}
