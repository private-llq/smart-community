package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 投票答案
 * @author: Hu
 * @create: 2021-08-23 16:42
 **/
@Data
@TableName("t_vote_option")
public class VoteOptionEntity extends BaseEntity {

    /**
     * 投票id
     */
    private Long voteId;
    /**
     * 投票答案
     */
    private String content;
    /**
     * 答案总投票人数
     */
    @TableField(exist = false)
    private Integer number;
    /**
     * 投票题目id
     */
    private String topicId;

    /**
     * 1一选项，2而选项，3三选项。。。。。
     */
    private Integer code;
    /**
     * 0未选，1选中
     */
    @TableField(exist = false)
    private Integer status;

}
