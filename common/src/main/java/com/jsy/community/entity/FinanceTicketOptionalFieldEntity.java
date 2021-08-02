package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 财务票据可选字段表实体
 * @Date: 2021/8/2 11:33
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_ticket_optional_field")
public class FinanceTicketOptionalFieldEntity extends BaseEntity {
    // 位置类型;1:页眉区;2:表格区;3:页脚区
    private Integer locationType;
    // 字段名称
    private String name;
    // 字段英文名称
    private String nameEn;
}
