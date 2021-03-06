package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

 /**
  * @author Tian
  * @since 2021/8/10-10:46
  * @description 设备管理
  **/
@Data
@ApiModel("车禁模块-设备管理")
@TableName("t_car_equipment_manage")
public class CarEquipmentManageEntity extends BaseQO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;

     @TableField(exist = false)
     private String idStr;
     public String getIdStr(){
         return String.valueOf(id);
     }

    @ApiModelProperty(value = "uid")
    private String uid;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty("设备名称")
    private  String equipmentName;

    @ApiModelProperty("设备序列号")
    private  String equipmentNumber;

    @ApiModelProperty("物联网卡号")
    private  String internetNumber;

    @ApiModelProperty("设备位置id")
    private  String locationId;

    @ApiModelProperty("设备位置")
    @TableField(exist = false)
    private  String locationName;

    @ApiModelProperty("临时车模式id")
    private  String patternId;

    @ApiModelProperty("临时车模式")
    @TableField(exist = false)
    private  String patternName;

    @ApiModelProperty("设备状态 0：下线  1：上线")
    private String state;


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
    @TableLogic
    private  Long deleted;

     @ApiModelProperty("分页查询当前页")
	@TableField(exist = false)
     private Long page;

     @ApiModelProperty("分页查询每页数据条数")
	@TableField(exist = false)
     private Long size;

     @TableField(exist = false)
     private CarEquipmentManageEntity query;

}
