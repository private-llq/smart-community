package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品标签实体类")
@TableName("t_proprietor_market_label")
public class ProprietorMarketLabelEntity extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "标签id")
    private String labelId;
    @ApiModelProperty(value = "标签名")
    private String label;
    @ApiModelProperty(value = "社区id")
    private  Long communityId;


}
