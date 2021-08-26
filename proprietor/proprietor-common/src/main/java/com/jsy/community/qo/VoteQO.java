package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  业主投票入参
 * @author: Hu
 * @create: 2021-08-24 10:11
 **/
@Data
public class VoteQO implements Serializable {
    /**
     * 投票id
     */
    private Long id;

    /**
     * 投票id
     */
    private Long topicId;
    /**
     * 1单选，2多选
     */
    private Integer choose;

    /**
     * 问题答案id
     */

    private List<Long> options;
}
