package com.jsy.community.vo.property;

import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FinanceOrderAndCarOrHouseInfoVO implements Serializable {

    @ApiModelProperty(value = "账单信息")
    private PropertyFinanceOrderEntity financeOrder;

    @ApiModelProperty(value = "车位信息")
    private CarPositionEntity carInfo;

    @ApiModelProperty(value = "房屋信息")
    private HouseEntity houseInfo;


}
