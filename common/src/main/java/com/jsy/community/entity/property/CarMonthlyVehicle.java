package com.jsy.community.entity.property;


import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("t_car_monthly_vehicle")
public class CarMonthlyVehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * uuid
     */
    private String uid;

    /**
     * 所属小区id
     */
    private Long communityId;

    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 车主姓名
     */
    private String ownerName;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 包月方式 ID:接受收费标准的uid
     */
    private String monthlyMethodId;
    /**
     * 包月方式：收费设置里面的收费标准名字
     */
    private String monthlyMethodName;
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime endTime;
    /**
     * 包月费用
     */
    private BigDecimal monthlyFee;
    /**
     * 下发状态 0：未下发 1：已下发
     */
    private Integer distributionStatus;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 到期状态 0 :到期  1：未到期
     */
    @TableField(exist = false)
    private String expirationStatus;

    /**
     * 车位编号 (关联车位，前端通过查询传进来)
     */
    @ExcelProperty("车位编号")
    private String carPosition;

}
