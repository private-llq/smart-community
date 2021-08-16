package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
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
     * 活动主题
     */
    private Long communityId;
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
    private LocalDateTime beginActivityTime;
    /**
     * 活动结束时间
     */
    private LocalDateTime overActivityTime;
    /**
     * 活动报名开始时间
     */
    private LocalDateTime beginApplyTime;
    /**
     * 活动报名结束时间
     */
    private LocalDateTime overApplyTime;
    /**
     * 活动总人数
     */
    private Integer count;
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

}
