package com.jsy.community.qo.property;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("修改车位类型qo")
public class UpdateCartPositionTypeQO implements Serializable {
    @ApiModelProperty("Id")
    private Long id;


    @ApiModelProperty("车位类型名称")
    private String description;
}
