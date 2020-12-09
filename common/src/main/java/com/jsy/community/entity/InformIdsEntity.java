package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-08 13:48
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_inform_ids")
public class InformIdsEntity extends BaseEntity{

    @ApiModelProperty(value = "通知id")
    private Long inform_id;
    @ApiModelProperty(value = "社区id")
    private Long community_id;
    @ApiModelProperty(value = "所有收到通知的用户id集合或者数组，转成json字符串")
    private String ids;

}
