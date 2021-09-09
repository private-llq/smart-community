package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description: 短信发送记录
 * @author: DKS
 * @create: 2021-09-08 17:00
 **/
@Data
@TableName("t_sms_send_record")
public class SmsSendRecordEntity extends BaseEntity {
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "社区名字")
	@TableField(exist = false)
	private String communityName;
	
	@ApiModelProperty(value = "手机号")
	private String mobile;
	
	@ApiModelProperty(value = "内容")
	private String content;
	
	@ApiModelProperty(value = "状态1.成功2.失败")
	private Integer status;
	
	@ApiModelProperty(value = "状态1.成功2.失败")
	@TableField(exist = false)
	private String statusName;
}
