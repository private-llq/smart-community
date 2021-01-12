package com.jsy.community.qo;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@ApiModel("添加车辆信息")
public class RelationCarsQo implements Serializable {

    @ApiModelProperty("车辆id")
    private Long Id;// 85

    @Pattern(groups = {addCarValidated.class, proprietorCarValidated.class}, regexp = RegexUtils.REGEX_CAR_PLATE, message = "请输入一个正确的车牌号!")
    @NotNull(groups = {addCarValidated.class, proprietorCarValidated.class}, message = "车牌不能为空!")
    @ApiModelProperty("车牌号")
    private String carId;// 85
    @ApiModelProperty("车辆类型")
    private Integer carType;
    @ApiModelProperty("车辆图片")
    private String carImgURL;

    @NotNull(groups = {addCarValidated.class, proprietorCarValidated.class}, message = "行驶证图片不能为空!")
    @ApiModelProperty("行驶证图片地址")
    private String drivingLicenseUrl;

    @ApiModelProperty(value = "家属ID",hidden = true)
    private Long houseMemberId;

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


    private interface addCarValidated{}
    private interface proprietorCarValidated{}

}
