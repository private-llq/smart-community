package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 报修订单信息
 * </p>
 *
 * @author lihao
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_repair_order")
@ApiModel(value="RepairOrder对象", description="报修订单信息")
public class RepairOrderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "房屋报修id")
    private Long repairId;

    @ApiModelProperty(value = "订单编号")
    private String number;
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    

    @ApiModelProperty(value = "用户评价")
    private String comment;
    
    @ApiModelProperty(value = "评价类型 0 好评 1 差评")
    private Integer commentStatus;
    
    @ApiModelProperty(value = "评价图片")
    private String imgPath;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

}
