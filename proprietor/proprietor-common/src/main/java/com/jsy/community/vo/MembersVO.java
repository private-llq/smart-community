package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 房间成员返回类
 * @author: Hu
 * @create: 2021-08-17 15:05
 **/
@Data
public class MembersVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 关系
     */
    private Integer relation;
    /**
     * 关系
     */
    private String relationText;
}
