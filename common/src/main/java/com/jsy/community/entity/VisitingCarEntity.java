package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qq459799974
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="随行车辆", description="随行车辆")
@TableName("t_visiting_car")
public class VisitingCarEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    private String uid;

    @ApiModelProperty(value = "随行车辆车牌号")
    @Pattern(groups = {addCarValidatedGroup.class, updateCarValidatedGroup.class}, regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$", message = "请输入一个正确的车牌号!")
    @NotNull(groups = {addCarValidatedGroup.class, updateCarValidatedGroup.class}, message = "缺少随行车辆车牌号")
    private String carPlate;
    
    @ApiModelProperty(value = "随行车辆类型ID")
    @NotNull(groups = {addCarValidatedGroup.class, updateCarValidatedGroup.class}, message = "缺少随行车辆类型")
    private Integer carType;
    
    @ApiModelProperty(value = "随行车辆类型名", hidden = true)
    private String carTypeStr;
    
    /**
     * 添加随行车辆前端参数验证接口
     */
    public interface addCarValidatedGroup{}
    
    /**
     * 修改随行车辆前端参数验证接口
     */
    public interface updateCarValidatedGroup{}
}
