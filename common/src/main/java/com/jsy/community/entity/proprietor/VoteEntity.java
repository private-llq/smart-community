package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 业主投票
 * @author: Hu
 * @create: 2021-08-23 16:39
 **/
@Data
@TableName("t_vote")
public class VoteEntity extends BaseEntity {
    /**
     * 社区id
     */
    private String communityId;
    /**
     * 楼栋id
     */
    private String buildingId;
    /**
     * 楼栋id
     */
    @TableField(exist = false)
    private String scope;
    /**
     * 主题
     */
    private String theme;
    /**
     * 1待发布，2进行中，3已结束
     */
    private Integer voteStatus;
    /**
     * 0当前用户未投票，1当前用户已投票
     */
    @TableField(exist = false)
    private Integer status;
    /**
     * 开始时间
     */
    private LocalDateTime beginTime;
    /**
     * 结束时间
     */
    private LocalDateTime overTime;
    /**
     * 图片集合，以逗号分割
     */
    private String picture;
    /**
     * 1单选，2多选
     */
    private Integer choose;

    /**
     * 1发布，2撤销
     */
    private Integer issueStatus;
    /**
     * 当前投票能参与的最大人数
     */
    private Integer total;
    /**
     * 已投票人数
     */
    @TableField(exist = false)
    private Integer voteTotal;
    /**
     * 投票题目
     */
    @TableField(exist = false)
    private VoteTopicEntity voteTopicEntity;


}
