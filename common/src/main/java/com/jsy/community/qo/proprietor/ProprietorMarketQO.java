package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品发布接参数")
public class ProprietorMarketQO {
    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty(value = "商品名")
    private String goodsName;

    @ApiModelProperty(value = "价格")
    private String price;

    @ApiModelProperty(value = "商品说明")
    private String goodsExplain;

    @ApiModelProperty(value = "是否面议（0不面议 1面议  默认1）")
    private String negotiable;

    @ApiModelProperty(value = "标签id")
    private String labelId;

    @ApiModelProperty(value = "商品类别id")
    private String categoryId;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "图片")
    private String images;
}
