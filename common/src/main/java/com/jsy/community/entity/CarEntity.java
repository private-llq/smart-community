package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 车辆实体对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="车辆登记对象", description="车辆实体")
@TableName("t_car")
public class CarEntity extends BaseEntity {

    @ApiModelProperty(value = "所属人ID")
    @JsonIgnore
    private Long uid;

    @Range(groups = {addCarValidated.class, updateCarValidated.class}, min = 1, max = Integer.MAX_VALUE, message = "车位id不合法")
    @ApiModelProperty(value = "车位ID")
    private Long carPositionId;

    @Range(groups = {addCarValidated.class, updateCarValidated.class}, min = 1, max = Integer.MAX_VALUE, message = "社区id不合法")
    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @Pattern(groups = {addCarValidated.class, updateCarValidated.class}, regexp = RegexUtils.REGEX_CAR_PLATE, message = "请输入一个正确的车牌号!")
    @ApiModelProperty(value = "车辆牌照")
    private String carPlate;

    @Pattern(groups = {addCarValidated.class}, regexp = RegexUtils.REGEX_URL, message = "请提供一个正确的访问地址!")
    @ApiModelProperty(value = "车辆照片访问路径")
    private String carImageUrl;

    @Pattern(groups = {addCarValidated.class, updateCarValidated.class}, regexp = RegexUtils.REGEX_MOBILE, message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    @ApiModelProperty(value = "车主联系方式")
    private String contact;

    @NotBlank(groups = {addCarValidated.class, updateCarValidated.class}, message = "车辆所属人不能为空!")
    @ApiModelProperty(value = "车辆所属人")
    private String owner;

    @NotBlank(groups = {addCarValidated.class, updateCarValidated.class}, message = "车辆类型未选择!")
    @ApiModelProperty(value = "车辆类型")
    private String carType;

    @ApiModelProperty(value = "是否通过审核")
    private Integer checkStatus;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone="GMT+8")
    private Date checkTime;

    public static CarEntity getInstance(){
        return new CarEntity();
    }

    /**
     * 登记车辆前端参数验证接口
     */
    public interface addCarValidated{}

    /**
     * 更新车辆前端参数验证接口
     */
    public interface updateCarValidated{}



}
