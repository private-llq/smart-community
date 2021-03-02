package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @return
 * @Author lihao
 * @Description 缴费类型
 * @Date 2020/12/11 11:01
 * @Param
 **/
@Data
@TableName("t_pay_type")
@ApiModel(value="PayType对象", description="缴费类型")
public class PayTypeEntity extends BaseEntity {

    @ApiModelProperty(value = "缴费类型")
    private String name;

    @ApiModelProperty(value = "图片地址")
    private String icon;

    @ApiModelProperty(value = "中号图片地址")
    private String mediumIcon;

    @ApiModelProperty(value = "大号图片地址")
    private String largeSizeIcon;

}
