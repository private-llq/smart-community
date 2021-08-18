package com.jsy.community.qo;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 房屋成员入参
 * @author: Hu
 * @create: 2021-08-18 09:14
 **/
public class MembersQO implements Serializable {

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
}
