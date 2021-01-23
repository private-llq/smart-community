package com.jsy.community.qo.proprietor;

import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


/**
 * 数据传输对象
 * 这个类的作用主要用于接收 和车辆相关的改 前端参数
 * @author YuLF
 * @since 2020-11-28 13:36
 */
@Data
@ApiModel("车辆接收参数对象")
public class CarQO implements Serializable {


    @NotNull(groups = {UpdateCarValidated.class}, message = "id不合法")
    @ApiModelProperty(value = "ID")
    private Long id;

    @Range(groups = {UpdateCarValidated.class}, min = 1,  message = "车位id不合法")
    @ApiModelProperty(value = "车位ID")
    private Long carPositionId;

    @Range(groups = {UpdateCarValidated.class}, min = 1,  message = "社区id不合法")
    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @Pattern(groups = { UpdateCarValidated.class}, regexp = RegexUtils.REGEX_MOBILE, message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    @ApiModelProperty(value = "车主联系方式")
    private String contact;

    @Pattern(groups = { UpdateCarValidated.class},regexp = RegexUtils.REGEX_REAL_NAME, message = "车辆所属人不能为空!")
    @ApiModelProperty(value = "车辆所属人")
    private String owner;

    @Range(groups = { UpdateCarValidated.class }, min = BusinessEnum.CarTypeEnum.CARTYPE_MIN, max = BusinessEnum.CarTypeEnum.CARTYPE_MAX, message = "车辆类型选择错误!")
    @ApiModelProperty(value = "车辆类型")
    private Integer carType;

    @Pattern(groups = {UpdateCarValidated.class}, regexp = RegexUtils.REGEX_CAR_PLATE, message = "请输入一个正确的车牌号!")
    @ApiModelProperty(value = "车辆牌照")
    private String carPlate;

    @Pattern(groups = { UpdateCarValidated.class}, regexp = RegexUtils.REGEX_URL, message = "请提供一个正确车辆图片的访问地址!")
    @ApiModelProperty(value = "车辆照片访问路径")
    private String carImageUrl;


    /**
     * 更新车辆前端参数验证接口
     */
    public interface UpdateCarValidated {}

}
