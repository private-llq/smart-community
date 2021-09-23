package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 投票人员
 * @author: Hu
 * @create: 2021-08-23 16:45
 **/
@Data
@TableName("t_vote_user")
public class VoteUserEntity implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String realName;
    /**
     * 投票id
     */
    private String voteId;
    /**
     * 投票id
     */
    private String topicId;
    /**
     * 答案id
     */
    private String optionId;
    /**
     * 答案id
     */
    private LocalDateTime createTime;
}
