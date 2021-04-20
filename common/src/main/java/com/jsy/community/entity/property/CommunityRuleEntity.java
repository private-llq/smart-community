package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-20 15:49
 **/
@Data
@ApiModel("小区收费标准中间表")
@TableName("t_community_rule")
public class CommunityRuleEntity {
    private Long communityId;
    private Long ruleId;
}
