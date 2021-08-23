package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品类别实体类")
@TableName("t_proprietor_market_category")
public class ProprietorMarketCategoryEntity implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "标签id")
    private String labelId;
    @ApiModelProperty(value = "类别名")
    private String category;
}
