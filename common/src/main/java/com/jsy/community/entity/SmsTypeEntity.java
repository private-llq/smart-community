package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: 短信分类
 * @author: DKS
 * @since: 2021/12/8 10:39
 */
@Data
@TableName("t_sms_type")
public class SmsTypeEntity extends BaseEntity {
    
    // 短信分类名称
    private String name;
}
