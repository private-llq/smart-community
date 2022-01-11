package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

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
    @Range( min = 0, message = "短信数量不能为负")
    private Integer number;
    
    // 价格
    @Range( min = 0, message = "价格不能为负")
    private BigDecimal price;
    
    // 排序
    @Range( min = 0, message = "排序不能为负")
    private Integer sort;
}
