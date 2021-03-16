package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-09 16:40
 **/
@Data
public class HouseTypeVo implements Serializable {
    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
}
