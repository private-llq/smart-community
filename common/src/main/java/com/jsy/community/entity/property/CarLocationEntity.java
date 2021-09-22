package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("设备管理-设备位置")
@TableName("t_car_equipment_location")
public class CarLocationEntity extends BaseQO {
    @ApiModelProperty("主键id")
        private Long id;

    @TableField(exist = false)
    private String idStr;
    public String getIdStr(){
        return String.valueOf(id);
    }

    @ApiModelProperty("设备位置")
    private String equipmentLocation;

    @ApiModelProperty("位置id")
    private String locationId;

    @ApiModelProperty("社区id")
    private Long communityId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;


    @ApiModelProperty("分页查询当前页")
    @TableField(exist = false)
    private Long page;

    @ApiModelProperty("分页查询每页数据条数")
    @TableField(exist = false)
    private Long size;

    @TableField(exist = false)
    private CarLocationEntity query;
}
