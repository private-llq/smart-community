package com.jsy.community.qo;


import lombok.Data;

@Data
public class CarMonthlyVehicleQO extends BaseQO<String>  {
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
     * 包月方式
     */
    private Integer monthlyMethod;

    /**
     * 到期状态 0：到期 1：未到期
     */
    private Integer expirationStatus;

    /**
     * 车位编号
     */
    private String carPosition;

    /**
     * Long communityId
     */
    private Long communityId;

}
