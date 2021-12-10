package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 短信套餐
 * @author: DKS
 * @since: 2021/12/9 10:53
 */
@Data
@TableName("t_sms_menu")
public class SmsMenuEntity extends BaseEntity {
    
    // 短信数量
    private Integer number;
    
    // 价格
    private BigDecimal price;
    
    // 排序
    private Integer sort;
}
