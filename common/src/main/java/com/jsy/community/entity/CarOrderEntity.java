package com.jsy.community.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
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
    @ExcelIgnore
    private Long carId;
    /**
     *  业主uid
     */
    @ExcelIgnore
    private String uid;
    /**
     *  社区id
     */
    @ExcelIgnore
    private Long communityId;
    /**
     *  车位id
     */
    @ExcelIgnore
    private Long carPositionId;

    /**
     *  车位编号
     */
    @ExcelIgnore
    @TableField(exist = false)
    private String carPositionText;
    /**
     *  1临时收费，2月租收费
     */
    @ExcelIgnore
    private Integer type;
    /*车牌颜色*/
    @ExcelIgnore
    private String plateColor;
    /**
     *  1临时收费，2月租收费
     */
    @ExcelIgnore
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
     *  账单编号
     */
    private String billNum;
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
    private Integer month;
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
    /**
     * 扫码时间
     */
    @ExcelIgnore
    private LocalDateTime scanTime;
    /**
     * 是否出闸0为出闸1已出闸
     */
    @ExcelIgnore
    private Integer goStatus;

    //是否为滞留订单0正常,1滞留订单
    @ExcelIgnore
    private  Integer isRetention;

    /**
     * 停车时长
     */
    @TableField(exist = false)
    private String stopCarTime;
}
