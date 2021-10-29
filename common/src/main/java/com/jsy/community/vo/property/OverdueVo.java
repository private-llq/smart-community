package com.jsy.community.vo.property;

import com.jsy.community.entity.property.CarMonthlyVehicle;
import lombok.Data;

import java.io.Serializable;

@Data
public class OverdueVo implements Serializable {

    /**
     * 状态
     */
    private Integer state;
    /**
     * 返回对象
     */
    private CarMonthlyVehicle carMonthlyVehicle;


}
