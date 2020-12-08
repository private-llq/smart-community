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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qq459799974
 * @since 2020-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TVisitor对象", description="来访人员")
@TableName("t_visitor")
public class VisitorEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "随行人员对象")
    @TableField(exist = false)
    private List<VisitorPersonEntity> visitorPersonList;
    
    @ApiModelProperty(value = "随行车辆对象")
    @TableField(exist = false)
    private List<VisitingCarEntity> visitingCarList;
    
    @ApiModelProperty(value = "社区ID")
    @NotNull(message = "缺少社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "楼栋ID")
    @NotNull(message = "缺少楼栋ID")
    private Long buildingId;
    
    @JsonIgnore
    @ApiModelProperty(value = "业主ID")
    private String uid;

    @ApiModelProperty(value = "来访人姓名")
    @NotEmpty(message = "缺少来访人姓名")
    private String name;

    @ApiModelProperty(value = "所属门牌号")
    @NotEmpty(message = "缺少详细地址")
    private String address;

    @ApiModelProperty(value = "来访事由")
    private String reason;
    
    @ApiModelProperty(value = "预期来访时间")
    @NotNull(message = "缺少预期来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectTime;

    @ApiModelProperty(value = "来访人联系方式")
    @Pattern(regexp = "^1[3|4|5|7|8][0-9]{9}$", message = "请输入一个正确的手机号码 电信丨联通丨移动!")
    private String contact;
    
    @JsonIgnore
    @ApiModelProperty(value = "来访人身份证")
    private String idCard;
    
    @JsonIgnore
    @ApiModelProperty(value = "实际来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitedTime;
    
    @ApiModelProperty(value = "是否授予来访人社区门禁权限，0无，1二维码通行证，2人脸识别")
    private Integer isCommunityAccess;
    
    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限，0无，1二维码通行证，2可视对讲")
    private Integer isBuildingAccess;
    
    @JsonIgnore
    @ApiModelProperty(value = "审核方式，1业主审核，2物业审核")
    private Integer checkType;
    
    @JsonIgnore
    @ApiModelProperty(value = "是否审核，0未审核，1通过，2拒绝")
    private Integer checkStatus;
    
    @JsonIgnore
    @ApiModelProperty(value = "审核时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;
    
    @JsonIgnore
    @ApiModelProperty(value = "审核拒绝原因")
    private String refuseReason;
    
}
