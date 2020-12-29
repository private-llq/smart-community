package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-23 11:01
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_complain")
@ApiModel(value = "complain对象", description = "接收社区投诉建议")
public class ComplainEntity implements Serializable {
    @ApiModelProperty(value = "投诉建议id")
    private Long id;
    @ApiModelProperty(value = "类型，1投诉，2建议")
    private Integer type;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(hidden = true)
    private Integer deleted;
    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "图片地址")
    private String images;
    @ApiModelProperty(value = "投诉人")
    private String uid;
    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;
    @ApiModelProperty(value = "投诉状态")
    private Integer status;
    @ApiModelProperty(value = "反馈内容")
    private String feedback;
}