package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("社区集市实体类")
@TableName("t_proprietor_market")
public class ProprietorMarketEntity extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "发布人uid")
    private String uid;

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
    @ApiModelProperty(value = "上下架（0下架  1上架）")
    private String state;
    @ApiModelProperty(value = "商品类别id")
    private String categoryId;
    @ApiModelProperty(value = "点击率")
    private String click;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "图片")
    private String images;


}
