package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("绑定用户qo")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBindingQO implements Serializable {


    private static final long serialVersionUID = 1L;
    /**
     * 车位id
     */
    @ApiModelProperty
    private Long Id;


    /**
     * 所属业主
     */
    @ApiModelProperty
    private String uid;

    /**
     * 车位状态（0空置，1业主自用，2租赁）
     */
    @ApiModelProperty("车位状态(0空置,1业主自用,2租赁)")
    private Integer carPosStatus;

    /**
     * 所属房屋
     */
    @ApiModelProperty("所属房屋")
    private String belongHouse;

    /**
     * 所属房屋ID
     */
    @ApiModelProperty("所属房屋id")
    private Long houseId;

    /**
     * 业主电话
     */
    @ApiModelProperty("业主电话")
    private String ownerPhone;

    @ApiModelProperty("用户姓名")
    private String userName;



    @ApiModelProperty("几个月")
    private Integer number;

}
