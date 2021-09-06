package com.jsy.community.qo.property;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 修改收费项目接收类
 * @author: Hu
 * @create: 2021-09-06 14:00
 **/
@Data
public class UpdateRelevanceQO implements Serializable {
    /**
     * 收费项目id
     */
    private Long id;
    /**
     * 关联的车位或者房屋ids  逗号隔开
     */
    private String ids;

    /**
     * 关联类型  1房屋2车位
     */
    private Integer type;
}
