package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 养老导入家属返回类
 * @author: Hu
 * @create: 2021-12-02 15:29
 **/
@Data
public class HouseMembersQO implements Serializable {
    /**
     * 用户uid
     */
    private String uid;
    /**
     * 电话
     */
    private String mobile;
    /**
     * 姓名
     */
    private String name;
}
