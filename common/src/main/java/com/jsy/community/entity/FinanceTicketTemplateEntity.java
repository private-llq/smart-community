package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: Pipi
 * @Description: 票据打印模板信息表
 * @Date: 2021/8/2 14:02
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_ticket_template")
public class FinanceTicketTemplateEntity extends BaseEntity {
    // 打印模板名称
    @NotBlank(message = "请填写打印模板名称")
    private String name;
    // 模板类型;1:缴费单;2:收据
    @NotNull(message = "请选择模板类型;1:缴费单;2:收据")
    private Integer templateType;
    // 收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板
    @NotNull(message = "请选择收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板")
    private Integer chargeType;
}
