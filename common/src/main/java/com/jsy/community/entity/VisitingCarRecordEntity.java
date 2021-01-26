package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: chq459799974
 * @Date: 2020/12/10
**/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="随行车辆记录", description="随行车辆记录")
@TableName("t_visiting_car_record")
public class VisitingCarRecordEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    //访客记录ID
    private Long visitorId;

    @ApiModelProperty(value = "随行车辆车牌")
    @Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$", message = "请输入一个正确的车牌号!")
    private String carPlate;
    
    @ApiModelProperty(value = "随行车辆类型ID")
    @Range(min = 1, max = 5, message = "随行车辆类型不正确")
    private Integer carType;
    
    @ApiModelProperty(value = "随行车辆类型名", hidden = true)
    private String carTypeStr;
    
}
