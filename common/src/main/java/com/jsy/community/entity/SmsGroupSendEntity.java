package com.jsy.community.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description: 短信群发
 * @author: DKS
 * @create: 2021-09-02 10:23
 **/
@Data
public class SmsGroupSendEntity extends BaseEntity {
    
    @ApiModelProperty(value = "短信模板")
    private String SmsTemplate;
    
    @ApiModelProperty(value = "是否去重")
    private Boolean isDistinct;
    
    @ApiModelProperty(value = "定时发送时间")
    private String taskTime;
    
    @ApiModelProperty(value = "短信条数")
    private Integer number;
    
    @ApiModelProperty(value = "内容变量")
    private String content;
    
    @ApiModelProperty(value = "电话变量")
    private String tel;
    
    @ApiModelProperty(value = "验证码变量")
    private Integer code;
    
    @ApiModelProperty(value = "姓名变量")
    private String realName;
    
    @ApiModelProperty(value = "时间变量")
    private String costTime;
    
    @ApiModelProperty(value = "区域/目标变量")
    private String region;
    
    @ApiModelProperty(value = "费用变量")
    private BigDecimal price;
    
    @ApiModelProperty(value = "车牌号变量")
    private String carNum;
    
    @ApiModelProperty(value = "链接变量")
    private String linkWay;

}
