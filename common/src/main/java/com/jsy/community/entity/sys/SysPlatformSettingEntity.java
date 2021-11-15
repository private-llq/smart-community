package com.jsy.community.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 大后台平台设置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_sys_platform_setting")
public class SysPlatformSettingEntity extends BaseEntity {
	
	@ApiModelProperty(value = "高德地图key")
	private String mapKey;
	
	@ApiModelProperty(value = "用户协议")
	private String userAgreement;
	
	@ApiModelProperty(value = "隐私政策")
	private String privacyPolicy;
	
	@ApiModelProperty(value = "关于我们")
	private String aboutAs;
}
