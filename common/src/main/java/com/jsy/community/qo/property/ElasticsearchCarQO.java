package com.jsy.community.qo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-25 14:53
 **/
@Data
@ApiModel(value="物业车辆集合")
public class ElasticsearchCarQO implements Serializable {
    @ApiModelProperty(value = "车辆id")
    private Long id;
    @ApiModelProperty(value = "社区")
    private Long communityId;
    @ApiModelProperty(value = "车牌号")
    private String carPlate;
    @ApiModelProperty(value = "车辆类型： 1.微型车 2.小型车 3.紧凑型车 4.中型车 5.中大型车")
    private Integer carType;
    @ApiModelProperty(value = "车辆类型文本")
    private String carTypeText;
    @ApiModelProperty("真实姓名")
    private String owner;
    @ApiModelProperty("身份证")
    private String idCard;
    @ApiModelProperty("电话号码")
    private String mobile;
    @ApiModelProperty("车主身份1用户，2家属，3租户")
    private Integer ownerType;
    @ApiModelProperty("车主身份1用户，2家属，3租户")
    private String ownerTypeText;
    @ApiModelProperty("1业主uid,2家属id，3租户id")
    private String relationshipId;
    @ApiModelProperty("房屋id")
    private Long houseId;
    @ApiModelProperty("楼栋")
    private String building;
    @ApiModelProperty("楼层")
    private String floor;
    @ApiModelProperty("单元")
    private String unit;
    @ApiModelProperty("房屋")
    private String number;
    @ApiModelProperty("房屋类型")
    private Integer houseType;
    @ApiModelProperty("房屋类型")
    private String houseTypeText;
    @ApiModelProperty("创建时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:MM:ss")
//    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:MM:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
}
