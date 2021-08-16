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
@ApiModel("车禁模块-开闸记录")
@TableName("t_car_cut_off")
public class CarCutOffEntity  extends BaseEntity {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "车辆类型")
    private String carType;

    @ApiModelProperty(value = "开闸时间")
    private String openTime;

    @ApiModelProperty(value = "车道名称")
    private String  laneName;

    @ApiModelProperty(value = "进出方向")
    private String access;

    @ApiModelProperty(value = "操作者")
    private String  operator;

    @ApiModelProperty(value = "照片")
    private String image;

    @ApiModelProperty(value = "创建时间")
    //@TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    // @TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8")
    private LocalDateTime updateTime;


    @ApiModelProperty(value = "逻辑删除")
    @TableLogic(value = "0",delval = "1")
    private  Integer deleted;

}
