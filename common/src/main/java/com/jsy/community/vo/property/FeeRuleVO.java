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
    private String status;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 1固定金额~单价*面积，2单价*面积~单价*面积*周期
     */
    private Integer formula;

    /**
     * 计费公式
     */
    private String formulaName;

    /**
     * 计费方式1面积，2定额
     */
    private Integer chargeMode;
    /**
     * 计费方式
     */
    private Integer disposable;
    /**
     * 计费方式
     */
    private String chargeModeName;

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
}
