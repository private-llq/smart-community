package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-22 11:04
 **/
@Data
public class UserPropertyFinanceOrderVO implements Serializable {

    @ApiModelProperty(value = "编号")
    private String number;

    @ApiModelProperty(value = "名称")
    private String realName;
}
