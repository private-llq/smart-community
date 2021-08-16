package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("业主车辆")
@TableName("t_car_proprietor")
public class CarProprietorEntity extends BaseEntity {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty("车牌号")
    private String carNumber;

    @ApiModelProperty("业主姓名")
    private String proprietorName;

    @ApiModelProperty("手机号码")
    private Long phone;

    @ApiModelProperty(value = "创建时间")
    //@TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic(value = "0",delval = "1")
    private  Integer deleted;

}
