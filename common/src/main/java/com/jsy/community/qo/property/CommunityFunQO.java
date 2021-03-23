package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 14:08
 **/
@Data
@ApiModel("分页查询社区趣事")
public class CommunityFunQO implements Serializable {

//    @ApiModelProperty("分页查询当前页")
//    private Long page;
//
//    @ApiModelProperty("分页查询每页数据条数")
//    private Long size;

    @ApiModelProperty("社区趣事标题模糊查询")
    private String headline;

    @ApiModelProperty("状态1发布2撤销")
    private Integer status;

    @ApiModelProperty("社区趣事标签模糊查询")
    private String tallys;

    @ApiModelProperty("创建时间")
    private LocalDate creatrTimeStart;

    @ApiModelProperty("创建时间")
    private LocalDate creatrTimeOut;

    @ApiModelProperty("发布时间")
    private LocalDate issueTimeStart;

    @ApiModelProperty("发布时间")
    private LocalDate issueTimeOut;

}
