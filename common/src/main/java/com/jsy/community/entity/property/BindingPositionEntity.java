package com.jsy.community.entity.property;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("车辆绑定车位")
@TableName("t_car_binding_position")
public class BindingPositionEntity implements Serializable {
    /**
     * uuid
     */
    private String uid;
    /**
     *车位id
     */
    private String positionId;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 是否绑定 0：未绑定 1：已绑定
     */
    private Integer bindingStatus;
    /**
     * 社区id
     */
    private Long communityId;

}
