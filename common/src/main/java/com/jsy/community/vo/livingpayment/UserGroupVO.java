package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-18 09:59
 **/
@Data
@ApiModel("返回前端的自定义分组")
public class UserGroupVO implements Serializable {

    @ApiModelProperty(value = "户组ID")
    private String id;

    @ApiModelProperty(value = "户组名")
    private String name;

//    @ApiModelProperty(value = "1我家，2父母，3房东，4朋友，5其他")
//    private Integer type;
}
