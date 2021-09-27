package com.jsy.community.qo.property;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class orderChargeDto implements Serializable {
    /**
     * 社区名
     */
    private String communityName;


    /**
     * 车辆信息（车牌号）
     */
    private String carNumber;
    /**
     * 进库时间
     */
    private LocalDateTime inTime;
    /**
     * 停车时长
     */
    private String time;
    /**
     * 收费标准
     */
    private BigDecimal chargePrice;
    /**
     * 应缴金额
     */
    private BigDecimal money;
}
