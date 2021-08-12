package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 财务票据可选字段与票据类型关联表实体
 * @Date: 2021/8/7 16:43
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_ticket_optional_type_field")
public class FinanceTicketOptionalTypeFieldEntity {
    // 模板类型;1:缴费单;2:收据'
    private Integer templateType;
    // 收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板'
    private Integer chargeType;
    // 财务票据可选字段ID
    private Long fieldId;
}
