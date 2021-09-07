package com.jsy.community.qo.property;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 收费项目关联查询接收类
 * @author: Hu
 * @create: 2021-09-06 14:29
 **/
@Data
public class FeeRuleRelevanceQO implements Serializable {
    /**
     * 收费项目id
     */
    private Long id;
    /**
     * 模糊查询key
     */
    private String key;
    /**
     * 收费类型
     */
    private Integer type;
}
