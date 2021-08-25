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
    private Long communityId;
    /**
     * 主题
     */
    private String theme;
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
     * 当前投票能参与的最大人数
     */
    private Integer total;
    /**
     * 投票题目
     */
    @TableField(exist = false)
    private VoteTopicEntity voteTopicEntity;


}
