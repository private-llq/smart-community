package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: 短信模板
 * @author: DKS
 * @since: 2021/12/8 11:10
 */
@Data
@TableName("t_sms_template")
public class SmsTemplateEntity extends BaseEntity {
    
    // 短信分类id
    private Long smsTypeId;
    
    // 短信分类idStr
    @TableField(exist = false)
    private String smsTypeIdStr;
    
    // 短信模板名称
    private String name;
    
    // 模板id
    private String templateId;
    
    // 模板内容
    private String content;
    
    // 状态（1.启用 2.禁用）
    private Integer status;
    
    // 短信分类名称
    @TableField(exist = false)
    private String smsTypeIdName;
    
    // 状态名称
    @TableField(exist = false)
    private String statusName;
}
