package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @return
 * @Author lihao
 * @Description 缴费类型
 * @Date 2020/12/11 11:01
 * @Param
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pay_type")
@ApiModel(value="PayType对象", description="缴费类型")
public class PayTypeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "缴费类型")
    private String name;

    @ApiModelProperty(value = "图片地址")
    private String icon;

}
