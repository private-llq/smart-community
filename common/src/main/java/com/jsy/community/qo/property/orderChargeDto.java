package com.jsy.community.qo.property;


import com.alibaba.excel.annotation.ExcelIgnore;
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

    private Long id;

    /**
     * @Description: 
     * @Param: 社区id
     * @Return: 
     * @Author: Tian
     * @Date: 2021/10/18-15:51
     **/
    private Long communityId;

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

    /**
     * 订单编号
     */
    private String orderNum;


    /**
     * 是否支付 0 未支付 1 已支付
     */
    private Integer orderStatus;

    /**
     * 是否为房主代付订单  1 ：是 0：否
     */
    private Integer isPayAnother;
}
