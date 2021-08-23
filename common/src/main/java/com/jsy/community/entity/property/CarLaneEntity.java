package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 车道管理实体类
 */
@Data
@TableName("t_car_lane")
public class CarLaneEntity  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    //兼容H5，使用字符串格式， 针对js long型长度不够的问题
    @TableField(exist = false)
    private String idStr;
    public String getIdStr(){
        return String.valueOf(equipmentId);
    }

    /**
     * uuid
     */
    private String uid;
    /**
     * 所属小区id
     */
    private Long communityId;
    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String equipmentName;
    /**
     * 设备id
     */
    private Long equipmentId;
    /**
     * 车道名称
     */
    private String laneName;
    /**
     * 车道类型 0:入口 1：出口
     */
    private Integer laneType;
    /**
     * 设备安装到该车道的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime InstallationTime;

}
