package com.jsy.community.entity.property;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

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
     * 位置
     */
    private String position;
    /**
     * 金额
     */
    private BigDecimal money;





}
