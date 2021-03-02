package com.jsy.community.qo;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@ApiModel("添加车辆信息")
public class RelationCarsQo implements Serializable {

    @ApiModelProperty("车辆id")
    private Long Id;
    @Pattern(groups = {proprietorCarValidated.class}, regexp = RegexUtils.REGEX_CAR_PLATE, message = "请输入正确的车牌号!")
    @NotNull(groups = {proprietorCarValidated.class}, message = "车牌不能为空!")
    @NotBlank(groups = {proprietorCarValidated.class},message = "车牌不能为空")
    @ApiModelProperty("车牌号")
    private String carPlate;
    @ApiModelProperty("车辆类型")
    @NotNull(groups = {proprietorCarValidated.class},message = "车辆类型不能为空")
    private Integer carType;

    @NotNull(groups = {proprietorCarValidated.class}, message = "行驶证图片不能为空!")
    @NotBlank(groups = {proprietorCarValidated.class},message = "行驶证图片不能为空")
    @ApiModelProperty("行驶证图片地址")
    private String drivingLicenseUrl;

    @ApiModelProperty(value = "家属ID",hidden = true)
    private Long houseMemberId;

    //手机号
    @ApiModelProperty(hidden = true)
    private String contact;

    //用户ID
    @ApiModelProperty(hidden = true)
    private String uid;

    //家属名字
    @ApiModelProperty(hidden = true)
    private String owner;

    //所属社区
    @ApiModelProperty(hidden = true)
    private Long communityId;

    public interface proprietorCarValidated{}

}
