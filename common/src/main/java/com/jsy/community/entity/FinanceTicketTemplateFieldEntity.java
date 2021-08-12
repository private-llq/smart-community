package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联表
 * @Date: 2021/8/2 14:18
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_ticket_template_field")
public class FinanceTicketTemplateFieldEntity implements Serializable {
      //主键
      private String id;
      //票据模板ID
      @NotBlank(message = "票据模板ID不能为空")
      private String templateId;
      //字段ID
      @NotBlank(message = "字段ID不能为空")
      private String fieldId;
      //位置类型;1:页眉区;2:表格区;3:页脚区
      @NotNull(message = "位置类型不能为空;1:页眉区;2:表格区;3:页脚区")
      private Integer locationType;
      //字段名称
      @NotBlank(message = "字段名称不能为空")
      private String name;
      //字段英文名称
      @NotBlank(message = "字段英文名称不能为空")
      private String nameEn;
}