package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
  * @author Tian
  * @since 2021/8/3-14:10
  * @description 车禁-基础设置
  **/
@Data
@ApiModel("车禁-基础设置")
@TableName("t_car_basics")
public class CarBasicsEntity extends BaseEntity {
  @ApiModelProperty(value = "id")
  private Long id;


  @ApiModelProperty(value = "uid")
  private String uid;

  @ApiModelProperty(value = "社区id")
  private  Long communityId;

  @ApiModelProperty(value = "停留时间(分钟)")
 private Integer dwellTime;

  @ApiModelProperty(value = "临时车最大入场数（辆）")
 private Integer maxNumber;

  @ApiModelProperty(value = "临时车入场规则 0：选择 1：不选")
 private Integer rule;

  @ApiModelProperty(value = "特殊车辆收费（军警车）0：不收费  1：收费")
 private Integer exceptionCar;

  @ApiModelProperty(value = "用户包月（0：不包月  1：包月）")
 private Integer monthlyPayment;

  @ApiModelProperty(value = "月租车续费最大时长（n个月）")
 private Integer monthMaxTime;

  @ApiModelProperty(value = "未缴物业费是否允许包月（0：不允许  1：允许）")
 private Integer whetherAllowMonth;

//  @ApiModelProperty(value = "创建时间")
//  @TableField(exist = false)
//  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//  @JsonFormat(
//          pattern = "yyyy-MM-dd HH:mm:ss",
//          timezone = "GMT+8")
// private LocalDateTime createTime;
//
//  @ApiModelProperty(value = "更新时间")
//  @TableField(exist = false)
//  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//  @JsonFormat(
//          pattern = "yyyy-MM-dd HH:mm:ss",
//          timezone = "GMT+8")
// private LocalDateTime updateTime;
//
//  @ApiModelProperty(value = "逻辑删除")
//  @TableLogic(value = "0",delval = "1")
//  private  Integer deleted;

 }
