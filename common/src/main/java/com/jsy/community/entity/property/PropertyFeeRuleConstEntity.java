package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业缴费项目常量类
 * @author: Hu
 * @create: 2021-07-30 14:24
 **/
@Data
@TableName("t_property_fee_rule_const")
public class PropertyFeeRuleConstEntity implements Serializable {
    private Long id;
    private Integer code;
    private String name;
    private String english;
    private Long pid;
    @TableField(exist = false)
    private List<PropertyFeeRuleConstEntity> entityList=new LinkedList<>();

}
