package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 物业财务结算记录表实体
 * @Date: 2021/4/22 9:04
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_statement_record")
@ApiModel("物业财务结算记录表实体")
public class PropertyFinanceStatementRecordEntity extends BaseEntity {

    @ApiModelProperty("账单号")
    private String orderNum;

    @ApiModelProperty("结算单号")
    private String statementNum;

    @ApiModelProperty("操作类型,1:审核,2:结算,4:驳回")
    private Integer operationType;

    @ApiModelProperty("操作备注")
    private String remake;

    @ApiModelProperty("操作员ID")
    private Long operatorId;
}
