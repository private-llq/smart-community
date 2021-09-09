package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 缴费项目关联实体
 * @author: Hu
 * @create: 2021-09-06 09:30
 **/
@Data
@TableName("t_property_fee_rule_relevance")
public class PropertyFeeRuleRelevanceEntity implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 收费项目id
     */
    private Long ruleId;
    /**
     * 关联id
     */
    private Long relevanceId;
    /**
     * 关联类型：1房屋2车位
     */
    private Integer type;

}
