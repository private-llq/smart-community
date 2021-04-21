package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
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
public class VisitorEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "随行人员记录")
    @TableField(exist = false)
    private List<VisitorPersonRecordEntity> visitorPersonRecordList;
    
    @ApiModelProperty(value = "随行车辆记录")
    @TableField(exist = false)
    private List<VisitingCarRecordEntity> visitingCarRecordList;
    
    @ApiModelProperty(value = "社区ID")
    @NotNull(message = "缺少社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "楼栋ID")
    @NotNull(message = "缺少楼栋ID")
    private Long buildingId;
    
    @ApiModelProperty(value = "业主ID", hidden = true)
    private String uid;

    @ApiModelProperty(value = "来访人姓名")
    @NotEmpty(message = "缺少来访人姓名")
    private String name;

    @ApiModelProperty(value = "来访地址")
    @NotEmpty(message = "缺少详细地址")
    private String address;

    @ApiModelProperty(value = "来访事由ID 1.一般来访 2.应聘来访 3.走亲访友 4.客户来访")
    private Integer reason;
    
    @ApiModelProperty(value = "来访事由名")
    private String reasonStr;
    
    @ApiModelProperty(value = "预期来访开始时间")
    @NotNull(message = "缺少预期来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    
    @ApiModelProperty(value = "预期来访结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonIgnore
    private LocalDate endTime;

    @ApiModelProperty(value = "来访人联系方式")
    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    private String contact;
    
    @ApiModelProperty(value = "来访人身份证", hidden = true)
    private String idCard;
    
    @ApiModelProperty(value = "实际来访时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitedTime;
    
    @ApiModelProperty(value = "是否授予来访人社区门禁权限，0无，1二维码通行证，2人脸识别")
    @Range(min = 0,max = 2, message = "社区门禁授权选择出错")
    private Integer isCommunityAccess;
    
    @ApiModelProperty(value = "是否授予来访人社区门禁权限 文字描述", hidden = true)
    private String isCommunityAccessStr;
    
    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限，0无，1二维码通行证，2可视对讲")
    @Range(min = 0,max = 2, message = "楼栋门禁授权选择出错")
    private Integer isBuildingAccess;
    
    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限 文字描述", hidden = true)
    private String isBuildingAccessStr;
    
    @ApiModelProperty(value = "来访车辆车牌", hidden = true)
    private String carPlate;
    
    @ApiModelProperty(value = "来访车辆类型 1.微型车 2.小型车 3.紧凑型车 4.中型车 5.中大型车", hidden = true)
    @Range(min = 1, max = 5, message = "车辆类型不正确")
    private Integer carType;
    
    @ApiModelProperty(value = "来访车辆类型名", hidden = true)
    private String carTypeStr;
    
    @ApiModelProperty(value = "审核方式，1业主审核，2物业审核",hidden = true)
    private Integer checkType;
    
    @ApiModelProperty(value = "是否审核，0未审核，1通过，2拒绝", hidden = true)
    private Integer checkStatus;
    
    @ApiModelProperty(value = "审核时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;
    
    @ApiModelProperty(value = "审核拒绝原因", hidden = true)
    private String refuseReason;
    
    @ApiModelProperty(value = "状态 1.待入园 2.已入园 3.已出园 4.已失效", hidden = true)
    private Integer status;
    
    @ApiModelProperty(value = "创建时间别称", hidden = true)
    @TableField(exist = false)
    private LocalDateTime vCreateTime;
    
}
