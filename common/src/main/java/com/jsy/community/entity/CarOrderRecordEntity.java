package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-08-27 11:25
 **/
@Data
@TableName("t_car_order_record")
public class CarOrderRecordEntity implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * id
     */
    private String uid;
    /**
     * 车辆id
     */
    private Long carId;
    /**
     * 车辆id
     */
    private Long carPositionId;
    /**
     * 1绑定车辆，2月租续费
     */
    private Integer type;
    /**
     * 社区id
     */
    private Long communityId;
    /**
     * 车牌号
     */
    private String carPlate;
    /**
     * 月份
     */
    private Integer month;
    /**
     * 金额
     */
    private BigDecimal money;
    /**
     * 0未缴，1已缴
     */
    private Integer status;
    /**
     * 订单编号
     */
    private String orderNum;
    /**
     *  1微信支付，2支付宝支付
     */
    @TableField(exist = false)
    private Integer payType;

}
