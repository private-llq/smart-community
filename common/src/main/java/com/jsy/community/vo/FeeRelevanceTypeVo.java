package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 房屋或者车位编号返回类
 * @author: Hu
 * @create: 2021-03-09 16:40
 **/
@Data
public class FeeRelevanceTypeVo extends BaseVO {
    @ApiModelProperty(value = "名称")
    private String name;
}
