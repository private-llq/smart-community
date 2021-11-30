package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 缴费项目配置VO
 * @Date: 2021/11/12 17:58
 * @Version: 1.0
 **/
@Data
public class CebQueryPaymentBillParamModelVO implements Serializable {

    // 输入域优先级
    // 输入域优先级;1主输入域,2非主输入域
    private Integer priorLevel;

    // 输入域名称
    private String name;

    // 文本长度最小限制
    private Integer minFieldLength;

    // 文本长度最大限制
    private Integer maxFieldLength;

    // 说明
    private String description;

    // 下拉框选项
    // 下拉框选项例如2个选项：学杂费=1|通讯费=2
    private String listBoxOptions;

    // 表示filed字段
    private Integer filedNum;

    // 是否为空
    // 1可以为空;0表示不可以为空
    private Integer isNull;

    // 输入域的类型
    // 0表示文本框,1表示下拉框
    private Integer filedType;

    // 输入域类型
    // 0表示金额以分为单位;1表示金额以元为单位;2表示账期必须以YYYYMM格式
    private String inputType;

    // 显示优先级
    // 1-5输入框显示位置：1表示最前5表示最后
    private String showLevel;
}
