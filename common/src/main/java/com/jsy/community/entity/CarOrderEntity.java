package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 车辆缴费订单
 * @author: Hu
 * @create: 2021-08-25 15:25
 **/
@Data
@TableName("t_car_order")
public class CarOrderEntity extends BaseEntity {
    /**
     *  车辆id
     */
    private Long carId;
    /**
     *  业主uid
     */
    private String uid;
    /**
     *  社区id
     */
    private Long communityId;
    /**
     *  车位id
     */
    private Long carPositionId;
    /**
     *  车位编号
     */
    @TableField(exist = false)
    private String carPositionText;
    /**
     *  1临时收费，2月租收费
     */
    private Integer type;
    /**
     *  1临时收费，2月租收费
     */
    @TableField(exist = false)
    private String typeText;
    /**
     *  支付时间
     */
    private LocalDateTime orderTime;
    /**
     *  订单编号
     */
    private String orderNum;
    /**
     *  账单抬头
     */
    private String rise;
    /**
     *  周期开始时间
     */
    private LocalDateTime beginTime;
    /**
     *  周期结束时间
     */
    private LocalDateTime overTime;
    /**
     *  月份
     */
    private Long month;
    /**
     *  1线上支付，2线下支付
     */
    private Integer payType;
    /**
     *  支付金额
     */
    private BigDecimal money;
    /**
     *  0未支付，1已支付
     */
    private Integer orderStatus;
    /**
     *  车牌号
     */
    private String carPlate;

}