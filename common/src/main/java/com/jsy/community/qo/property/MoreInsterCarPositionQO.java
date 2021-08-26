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
@ApiModel("批量新增车位信息qo")
@NoArgsConstructor
@AllArgsConstructor
public class MoreInsterCarPositionQO implements Serializable {
    private static final long serialVersionUID = 1L;



    /**
     * 关联车位类型id
     */
    @ApiModelProperty("关联车位类型id")
    private Long typeId;

    /**
     * 起始车位
     */
    @ApiModelProperty("起始车位")
    private Integer start;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    private Integer number;
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
