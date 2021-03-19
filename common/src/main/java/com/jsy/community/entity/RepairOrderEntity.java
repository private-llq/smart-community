package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
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
    
    @ApiModelProperty(value = "报修人")//
    private String name;
    
    @ApiModelProperty(value = "联系电话")//
    private String phone;
    
    @ApiModelProperty(value = "报修地址")//
    private String address;
    
    @ApiModelProperty(value = "报修事项id")//
    private Long type;
    
    @ApiModelProperty(value = "报修事项字符串形式")//
    private String typeName;
    
    @ApiModelProperty(value = "报修内容")//
    private String problem;
    
    @ApiModelProperty(value = "报修图片地址")//
    private String repairImg;
    
    @ApiModelProperty(value = "下单时间")
    private Date orderTime;
    
    @ApiModelProperty(value = "报修类别 0 个人报修 1 公共报修")//
    private Integer repairType;
    
    @ApiModelProperty(value = "订单状态 0 待处理 1 处理中 2 已处理")//
    private Integer status;
    
    @ApiModelProperty(value = "报修金额")
    private BigDecimal money;
    
    
    
    
    
    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
    
    @ApiModelProperty(value = "社区id")//
    private Long communityId;

    @ApiModelProperty(value = "用户评价")//
    private String comment;
    
    @ApiModelProperty(value = "评价类型 0 好评 1 差评")
    private Integer commentStatus;
    
    @ApiModelProperty(value = "评价图片")
    private String imgPath;

}
