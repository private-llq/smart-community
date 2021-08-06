package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    @NotBlank(groups = {AddTicketTemplateValidate.class, UpdateTicketTemplateValidate.class}, message = "请填写打印模板名称")
    private String name;
    // 模板类型;1:缴费单;2:收据
    @NotNull(groups = {AddTicketTemplateValidate.class}, message = "请选择模板类型;1:缴费单;2:收据")
    private Integer templateType;
    // 收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板
    @NotNull(groups = {AddTicketTemplateValidate.class}, message = "请选择收费类型;1:水电气缴费模板;2:租金管理费模板;3:物业费/管理费模板;4:通用模板")
    private Integer chargeType;
    // 社区ID
    private String communityId;


    // 模板类型字符串
    @TableField(exist = false)
    private String templateTypeStr;

    // 收费类型收费类型字符
    @TableField(exist = false)
    private String chargeTypeStr;

    public interface AddTicketTemplateValidate{}
    public interface UpdateTicketTemplateValidate{}
}
