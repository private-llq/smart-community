package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 投票问题
 * @author: Hu
 * @create: 2021-08-24 09:27
 **/
@Data
@TableName("t_vote_topic")
public class VoteTopicEntity extends BaseEntity {
    /**
     * 投票id
     */
    private String voteId;
    /**
     * 问题内容
     */
    private String content;

}
