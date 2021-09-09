package com.jsy.community.qo.property;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class PropertyMarketQO extends BaseQO {
    @ApiModelProperty(value = "上下架（0下架  1上架）")
    private Integer state;

    @ApiModelProperty(value = "商品类别id")
    private String categoryId;

    @ApiModelProperty(value = "发布人uid")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String phone;

}
