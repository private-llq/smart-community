package com.jsy.community.entity.property;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_car_charge")
public class CarChargeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属小区id
     */
    private Long communityId;

    /**
     * uuid
     */
    private String uid;
    /**
     * 收费名称
     */
    private String name;
    /**
     * 收费类型 0：月租 1：临时
     */
    private Integer type;
    /**
     * 位置 0:地上 1：地下
     */
    private String position;
    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 临时->车牌类型 0 黄牌 1 蓝牌
     */
    private Integer plateType;
    /**
     * 临时->免费时间 单位/分钟
     */
    private Integer freeTime;
    /**
     * 临时->收费价格 元/时
     */
    private BigDecimal chargePrice;
    /**
     * 临时->封顶费用 单位/元
     */
    private BigDecimal cappingFee;

}
