package com.jsy.community.qo.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 主要用于接收 前端传过来的常量参数对象
 * 房屋租售常量收参对象
 * @author YuLF
 * @since  2021/1/13 17:59
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
public class HouseLeaseConstQO implements Serializable {


    private Long houseConstCode;

    /**
     * 常量名称
     */
    private String houseConstName;

    /**
     * 常量值
     */
    private String houseConstValue;

    /**
     * 常量类型
     */
    private String houseConstType;

    /**
     * 常量注释
     */
    private String annotation;

}
