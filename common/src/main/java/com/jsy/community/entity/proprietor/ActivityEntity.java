package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: APP活动实体类
 * @author: Hu
 * @create: 2021-08-13 14:26
 **/
@Data
@TableName("t_activity")
public class ActivityEntity extends BaseEntity {
    /**
     * 社区id
     */
    private Long communityId;
    /**
     * 1预发布，2报名进行中，3报名已结束，4活动进行中，5活动已结束
     */
    private Integer activityStatus;
    /**
     * 活动主题
     */
    private String theme;
    /**
     * 活动内筒
     */
    private String content;
    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime beginActivityTime;
    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime overActivityTime;
    /**
     * 活动报名开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime beginApplyTime;
    /**
     * 活动报名结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime overApplyTime;
    /**
     * 活动总人数
     */
    private Integer count;
    
    @ApiModelProperty(value = "1.由小区发布 2.由大后台发布")
    private Integer type;

    /**
     * 活动已报名总人数
     */
    @TableField(exist = false)
    private Integer applyCount;

    /**
     * 状态1已报名0未报名
     */
    @TableField(exist = false)
    private Integer status;
    /**
     * 活动图片
     */
    private String picture;

    /**
     * 报名人员电话
     */
    @TableField(exist = false)
    private String mobile;
    /**
     * 报名人员名称
     */
    @TableField(exist = false)
    private String name;
    
    /**
     * 1预发布，2报名进行中，3报名已结束，4活动进行中，5活动已结束
     */
    @TableField(exist = false)
    private String activityStatusName;
}
