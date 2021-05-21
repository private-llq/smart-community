package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-17 09:19
 **/
@Data
public class SelectCommunityFunQO implements Serializable {
    @ApiModelProperty("分页查询当前页")
    private Long page;

    @ApiModelProperty("分页查询每页数据条数")
    private Long size;

    @ApiModelProperty("社区趣事标题模糊查询")
    private String headline;

    @ApiModelProperty("社区id")
    private Long communityId;
}
