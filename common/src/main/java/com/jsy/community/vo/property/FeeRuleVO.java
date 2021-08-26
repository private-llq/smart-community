package com.jsy.community.vo.property;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description: 缴费项目返回数据
 * @author: Hu
 * @create: 2021-08-04 14:27
 **/
@Data
public class FeeRuleVO implements Serializable {
    /**
     * 数据id
     */
    private Long id;

    /**
     * 启用状态
     */
    private Integer status;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 计费公式
     */
    private String formula;

    /**
     * 计价方式
     */
    private String valuation;

    /**
     * 单价
     */
    private BigDecimal monetaryUnit;

    /**
     * 周期，1月，2季，3半年，4
     */
    private Integer period;

    /**
     * 周期，1月，2季，3半年，4
     */
    private String periodName;

    /**
     * 周期，1月，2季，3半年，4
     */
    private String relevance;

    /**
     * 报表展示0不展示，1展示
     */
    private Integer reportStatus;


}
