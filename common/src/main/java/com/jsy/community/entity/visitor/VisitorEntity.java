package com.jsy.community.entity.visitor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 来访人员
 * </p>
 *
 * @author jsy
 * @since 2020-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TVisitor对象", description="来访人员")
@TableName("t_visitor")
public class VisitorEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @ApiModelProperty(value = "业主ID")
    private Long uid;

    @ApiModelProperty(value = "来访人姓名")
    private String name;

    @ApiModelProperty(value = "所属单元")
    private String unit;

    @ApiModelProperty(value = "所属楼栋")
    private String building;

    @ApiModelProperty(value = "所属楼层")
    private String floor;

    @ApiModelProperty(value = "所属门牌号")
    private String door;

    @ApiModelProperty(value = "来访事由")
    private String reason;
    
    @ApiModelProperty(value = "预期来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectTime;

    @ApiModelProperty(value = "来访人联系方式")
    private String contact;

    @ApiModelProperty(value = "来访人身份证")
    private String idCard;
    
    @ApiModelProperty(value = "实际来访时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitedTime;

    @ApiModelProperty(value = "是否授予来访人社区门禁权限，0无，1临时密码，2人脸识别")
    private Integer isCommunityAccess;

    @ApiModelProperty(value = "是否授予来访人楼栋门禁权限，0无，1临时密码，2可视对讲")
    private Integer isBuildingAccess;

    @ApiModelProperty(value = "审核方式，1业主审核，2物业审核")
    private Integer checkType;

    @ApiModelProperty(value = "是否审核，0未审核，1通过，2拒绝")
    private Integer checkStatus;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    @ApiModelProperty(value = "审核拒绝原因")
    private String refuseReason;

}
