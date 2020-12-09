package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 14:08
 **/
@Data
@ApiModel("分页查询社区趣事")
public class CommunityFunQO implements Serializable {

    @ApiModelProperty("分页查询当前页")
    private Long page;

    @ApiModelProperty("分页查询每页数据条数")
    private Long size;

    @ApiModelProperty("社区趣事标题模糊查询")
    private String headline;

}
