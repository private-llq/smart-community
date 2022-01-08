package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 短信配置
 * @author: DKS
 * @since: 2021/12/6 11:16
 */
@Data
@TableName("t_sms_setting")
public class SmsEntity extends BaseEntity {
    
    @ApiModelProperty(value = "阿里云短信子账号accessKeyId")
    private String accessKeyId;
    
    @ApiModelProperty(value = "阿里云短信子账号secret")
    private String accessKeySecret;
    
    @ApiModelProperty(value = "短信签名")
    private String smsSign;
}
