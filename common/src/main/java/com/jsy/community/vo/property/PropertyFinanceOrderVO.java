package com.jsy.community.vo.property;

import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description:  物业账单返回类
 * @author: Hu
 * @create: 2021-04-22 10:14
 **/
@Data
public class PropertyFinanceOrderVO extends BaseVO {
    @ApiModelProperty(value = "支付单号")
    private String orderNum;
    @ApiModelProperty(value = "支付单号")
    private Long communityId;
    @ApiModelProperty(value = "应缴月份")
    private LocalDate orderTime;

    @ApiModelProperty(value = "房间id")
    private Long houseId;

    @ApiModelProperty(value = "收款状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "物业费")
    private BigDecimal propertyFee;
    @ApiModelProperty(value = "违约金")
    private BigDecimal penalSum;
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalMoney;


    @ApiModelProperty(value = "编号")
    private String number;
    @ApiModelProperty(value = "建筑面积(㎡)")
    private Double buildArea;
    @ApiModelProperty(value = "楼栋名")
    private String building;
    @ApiModelProperty(value = "单元名")
    private String unit;
    @ApiModelProperty(value = "楼层名")
    private String floor;
    @ApiModelProperty(value = "房屋类型1.商铺 2.住宅")
    private Integer houseType;
    @ApiModelProperty(value = "房屋类型1.商铺 2.住宅")
    private String houseTypeText;

}
