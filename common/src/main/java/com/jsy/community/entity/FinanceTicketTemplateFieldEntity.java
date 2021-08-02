package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 票据模板与字段关联表
 * @Date: 2021/8/2 14:18
 * @Version: 1.0
 **/
@Data
@TableName("t_property_finance_ticket_template_field")
public class FinanceTicketTemplateFieldEntity {
      //主键
      private String id;
      //票据模板ID
      private String templateId;
      //字段ID
      private String fieldId;
      //位置类型;1:页眉区;2:表格区;3:页脚区
      private String locationType;
      //字段名称
      private String name;
      //字段英文名称
      private String nameEn;
}
