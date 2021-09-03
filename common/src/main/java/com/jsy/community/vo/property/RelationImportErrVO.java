package com.jsy.community.vo.property;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description: 成员信息导入错误信息
 * @author: Hu
 * @create: 2021-09-03 15:36
 **/
@Data
public class RelationImportErrVO implements Serializable {
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 关系
     */
    private String relation;
    /**
     * 电话
     */
    private String mobile;
    /**
     * 楼栋
     */
    private String building;
    /**
     * 单元
     */
    private String unit;
    /**
     * 房间号
     */
    private String door;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 入驻时间
     */
    private LocalDate enterTime;
    /**
     * 入驻原因
     */
    private String enterReason;
    /**
     * 银行卡号
     */
    private String creditCard;
    /**
     * 成员单位
     */
    private String RelationUnit;
    /**
     * 错误提示
     */
    private String error;
}
