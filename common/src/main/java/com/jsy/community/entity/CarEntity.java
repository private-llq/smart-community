package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Date;

/**
 * 车辆实体对象
 * YuLF
 * 数据访问对象：这个类主要用于对应数据库表t_car的数据字段的映射关系，
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "车辆登记对象", description = "车辆实体")
@TableName("t_car")
public class CarEntity extends BaseEntity {

    @ApiModelProperty(value = "所属人ID")
    @JsonIgnore
    private String uid;

    @ApiModelProperty(value = "家属车辆ID")
    private Long houseMemberId;

    @ApiModelProperty(value = "车位ID")
    private Long carPositionId;

    @ApiModelProperty(value = "车位编号")
    @TableField(exist = false)
    private String carPositionText;

    @Range(groups = {AddCarValidated.class}, min = 1, message = "社区id不合法")
    @NotNull(groups = {AddCarValidated.class}, message = "社区不能为空")
    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @Pattern(groups = {AddCarValidated.class, ProprietorCarValidated.class, SaveCarValidated.class}, regexp = RegexUtils.REGEX_CAR_PLATE, message = "请输入一个正确的车牌号!")
    @NotNull(groups = {AddCarValidated.class, ProprietorCarValidated.class, SaveCarValidated.class}, message = "车牌不能为空!")
    @ApiModelProperty(value = "车辆牌照")
    private String carPlate;

    @Pattern(groups = {AddCarValidated.class, ProprietorCarValidated.class}, regexp = RegexUtils.REGEX_URL, message = "请提供一个正确的访问地址!")
    @NotNull(groups = {AddCarValidated.class, ProprietorCarValidated.class}, message = "车辆图片地址未提供!")
    @ApiModelProperty(value = "车辆照片访问路径")
    private String carImageUrl;


    @Pattern(groups = {AddCarValidated.class}, regexp = RegexUtils.REGEX_MOBILE, message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    @NotNull(groups = {AddCarValidated.class}, message = "手机号码未输入!")
    @ApiModelProperty(value = "车主联系方式")
    private String contact;

    @NotBlank(groups = {AddCarValidated.class}, message = "车辆所属人不能为空!")
    @ApiModelProperty(value = "车辆所属人")
    private String owner;

    @Range(groups = {AddCarValidated.class, ProprietorCarValidated.class}, min = BusinessEnum.CarTypeEnum.CAR_TYPE_MIN, max = BusinessEnum.CarTypeEnum.CAR_TYPE_MAX, message = "车辆类型选择错误!")
    @NotNull(groups = {ProprietorCarValidated.class}, message = "车辆类型未选择!")
    @ApiModelProperty(value = "车辆类型： 1.微型车 2.小型车 3.紧凑型车 4.中型车 5.中大型车")
    private Integer carType;

    @TableField(exist = false)
    @ApiModelProperty(value = "车辆类型文本")
    private String carTypeText;

    @ApiModelProperty(value = "是否通过审核")
    private Integer checkStatus;

    @ApiModelProperty(value = "行驶证图片地址")
    private String drivingLicenseUrl;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkTime;

    @ApiModelProperty(value = "1临时，2月租")
    private Integer type;

    @ApiModelProperty(value = "1临时，2月租")
    @TableField(exist = false)
    private String typeText;
    @ApiModelProperty(value = "月租开始时间")
    private LocalDate beginTime;
    @ApiModelProperty(value = "月租结束时间")
    private LocalDate overTime;

    @ApiModelProperty(value = "剩余天数")
    @TableField(exist = false)
    private Long remainingDays;


    public static CarEntity getInstance() {
        return new CarEntity();
    }


    /**
     * [业主信息和车辆信息登记]业主登记时的车辆参数验证
     */
    public interface ProprietorCarValidated {
    }

    /**
     * 单独[业主]登记车辆前端参数验证接口
     */
    public interface AddCarValidated {
    }

    /**
     * 新app添加修改车辆
     */
    public interface SaveCarValidated {
    }


}
