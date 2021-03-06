package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qq459799974
 * @since 2020-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="来访人员", description="来访人员")
@TableName("t_visitor")
public class VisitorEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区ID")
    @NotNull(groups = {queryVisitorCarValidate.class, addVisitorValidate.class, addTempCodeValidate.class}, message = "缺少社区ID")
    private Long communityId;

    @ApiModelProperty(value = "楼栋ID")
    @NotNull(groups = {addVisitorValidate.class}, message = "缺少楼栋ID")
    private String buildingId;

    @ApiModelProperty(value = "业主ID", hidden = true)
    private String uid;

    @ApiModelProperty(value = "来访人姓名")
    @NotBlank(groups = {addVisitorValidate.class}, message = "缺少来访人姓名")
    private String name;

    @ApiModelProperty(value = "来访地址")
    @NotEmpty(groups = {addVisitorValidate.class}, message = "缺少详细地址")
    private String address;

    @ApiModelProperty(value = "来访事由ID 1.一般来访 2.应聘来访 3.走亲访友 4.客户来访")
    private Integer reason;

    @ApiModelProperty(value = "预期来访开始时间")
    @NotNull(groups = {addVisitorValidate.class}, message = "缺少预期来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "预期来访结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull(groups = {addVisitorValidate.class}, message = "缺少预期来访结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "来访人联系方式")
    @Pattern(groups = {addVisitorValidate.class}, regexp = "^1[3|4|5|6|7|8|9][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    @NotBlank(groups = {addVisitorValidate.class}, message = "缺少来访人联系方式")
    private String contact;

    @ApiModelProperty(value = "来访人身份证", hidden = true)
    private String idCard;

    @ApiModelProperty(value = "实际来访时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitedTime;

    @ApiModelProperty(value = "是否授予来访人社区门禁权限，0无，1二维码通行证，2人脸识别")
    @Range(min = 0,max = 2, message = "社区门禁授权选择出错")
    private Integer isCommunityAccess;

    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限，0无，1二维码通行证，2可视对讲")
    @Range(min = 0,max = 2, message = "楼栋门禁授权选择出错")
    private Integer isCarBanAccess;

    @ApiModelProperty(value = "来访车辆车牌", hidden = true)
    private String carPlate;

    // 车费代缴状态;0:不代缴;1:代缴
    private Integer carAlternativePaymentStatus;

    @ApiModelProperty(value = "来访车辆类型 1.微型车 2.小型车 3.紧凑型车 4.中型车 5.中大型车 6.其他车型", hidden = true)
    private Integer carType;

    @ApiModelProperty(value = "审核方式，1业主审核，2物业审核")
    private Integer checkType;

    @ApiModelProperty(value = "是否审核，0未审核，1通过，2拒绝")
    private Integer checkStatus;

    @ApiModelProperty(value = "审核时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    @ApiModelProperty(value = "审核拒绝原因", hidden = true)
    private String refuseReason;

    @ApiModelProperty(value = "状态 1.待入园 2.已入园 3.已出园 4.已失效", hidden = true)
    private Integer status;

    // 人脸图片地址
    private String faceUrl;

    // 临时通行码有效时间分钟数
    @NotNull(groups = {addTempCodeValidate.class}, message = "临时通行码有效时间不能为空")
    private Integer effectiveTime;

    // 是否是临时通行码;0:不是;1是;
    private Integer tempCodeStatus;

    @ApiModelProperty(value = "来访事由名")
    @TableField(exist = false)
    private String reasonStr;

    @ApiModelProperty(value = "是否授予来访人社区门禁权限 文字描述", hidden = true)
    @TableField(exist = false)
    private String isCommunityAccessStr;

    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限 文字描述", hidden = true)
    @TableField(exist = false)
    private String isCarBanAccessStr;

    @ApiModelProperty(value = "来访车辆类型名", hidden = true)
    @TableField(exist = false)
    private String carTypeStr;

    @ApiModelProperty(value = "创建时间别称", hidden = true)
    @TableField(exist = false)
    private LocalDateTime vCreateTime;

    @ApiModelProperty(value = "随行人员记录")
    @TableField(exist = false)
    private List<VisitorPersonRecordEntity> visitorPersonRecordList;

    @ApiModelProperty(value = "随行车辆记录")
    @TableField(exist = false)
    private List<VisitingCarRecordEntity> visitingCarRecordList;

    // 授权人姓名
    @TableField(exist = false)
    private String nameOfAuthorizedPerson;

    // 授权人手机
    @TableField(exist = false)
    private String mobileOfAuthorizedPerson;
    // 有效分钟数
    @TableField(exist = false)
    private Integer effectiveMinutes;

    // 审核方式字符串
    @TableField(exist = false)
    private String checkTypeStr;

    // 审核状态字符串
    @TableField(exist = false)
    private String checkStatusStr;

    // 邀请过期状态;0:未过期;1已过期;
    @TableField(exist = false)
    private Integer expireStatus;

    public interface addVisitorValidate{}

    // 查询邀请过的车俩验证组
    public interface queryVisitorCarValidate{}

    // 生成临时通行二维码验证组
    public interface addTempCodeValidate{}

}
