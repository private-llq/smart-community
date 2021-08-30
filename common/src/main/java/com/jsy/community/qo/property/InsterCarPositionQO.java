package com.jsy.community.qo.property;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
@ApiModel("新增车位信息qo")
@NoArgsConstructor
@AllArgsConstructor
public class InsterCarPositionQO implements Serializable {


    private static final long serialVersionUID = 1L;



    /**
     * 关联车位类型id
     */
    @ApiModelProperty("关联车位类型id")
    private Long typeId;


    /**
     * 车位号
     */
    @ApiModelProperty("车位号")
    private String carPosition;


    /**
     * 产权面积
     */
    @ApiModelProperty("产权面积(m²)")
    private Double area;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;


    /**
     * 售价
     */
    @ApiModelProperty("售价")
    private BigDecimal price;
    /**
     * 月租
     */
    @ApiModelProperty("月租")
    private  BigDecimal     monthlyPrice;

}
