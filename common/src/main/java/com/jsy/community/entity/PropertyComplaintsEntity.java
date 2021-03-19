package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-17 10:25
 **/
@Data
@TableName("t_property_complaints")
@ApiModel(value = "PropertyComplaints对象", description = "物业投诉")
public class PropertyComplaintsEntity extends BaseEntity {

    @ApiModelProperty(value = "编号")
    private String serialNumber;
    @ApiModelProperty(value = "投诉人")
    private String uid;
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    @ApiModelProperty(value = "投诉人名称")
    private String name;
    @ApiModelProperty(value = "投诉人电话")
    private String mobile;
    @ApiModelProperty(value = "投诉原因,1质量投诉，2维修投诉，3扰民投诉，4安全投诉，" +
            "5停车管理投诉，6环境投诉，7设备设施，8服务投诉，9费用投诉，10其他投诉")
    private Integer type;
    @ApiModelProperty(value = "投诉内容")
    private String content;
    @ApiModelProperty(value = "地点位置")
    private String location;
    @ApiModelProperty(value = "图片地址")
    private String images;
    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;
    @ApiModelProperty(value = "投诉状态0未回复1已回复")
    private Integer status;
    @ApiModelProperty(value = "回复人")
    private String replyUid;
    @ApiModelProperty(value = "回复时间")
    private LocalDateTime replyTime;
    @ApiModelProperty(value = "回复类容")
    private String replyContent;
    @ApiModelProperty(value = "回复人名称")
    private String replyName;

}
