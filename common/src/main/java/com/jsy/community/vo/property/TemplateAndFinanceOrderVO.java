package com.jsy.community.vo.property;

import com.jsy.community.entity.FinanceTicketTemplateFieldEntity;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TemplateAndFinanceOrderVO implements Serializable {

    @ApiModelProperty(value = "模板")
    private Map<Integer, List<FinanceTicketTemplateFieldEntity>> template = new HashMap<>();

    @ApiModelProperty(value = "数据")
    private PropertyFinanceOrderEntity financeOrder;

}
