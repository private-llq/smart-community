package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 10:22
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("业主实体类")
@TableName("t_community_fun")
public class CommunityFunEntity extends BaseEntity {
    @ApiModelProperty(value = "社区趣事标题")
    @TableField("description")
    private String description;
    @ApiModelProperty(value = "社区趣事内容")
    private String content;
    @ApiModelProperty(value = "社区趣事图片地址")
    private String imageUrl;
    @ApiModelProperty(value = "社区趣事状态1表示未上线，2二表示已上线")
    private Integer status;
    @ApiModelProperty(value = "上线时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "下线时间")
    private LocalDateTime outTime;


}
