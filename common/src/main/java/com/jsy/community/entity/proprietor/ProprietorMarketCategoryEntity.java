package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("商品类别实体类")
@TableName("t_proprietor_market_category")
public class ProprietorMarketCategoryEntity extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "标签id")
    private String categoryId;

    @ApiModelProperty(value = "标签排序字段")
    @Range( min = 0, message = "排序不能为负")
    private Integer sort;

    @NotNull
    @ApiModelProperty(value = "类别名")
    private String category;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;
}
