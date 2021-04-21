package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description:  物业车辆记录表
 * @author: Hu
 * @create: 2021-03-27 11:37
 **/
@Data
@TableName("t_property_car")
public class PropertyCarEntity extends BaseEntity {
    private Long communityId;
    private Long houseId;
    private Integer ownerType;
    private String owner;
    private String mobile;
    private String idCard;
    private String ownerTypeText;
    private String relationshipId;
    private Integer carType;
    private String carTypeText;
    private String carPlate;
    private String number;
    private String building;
    private String floor;
    private String unit;
    private Integer houseType;
    private String houseTypeText;

}
