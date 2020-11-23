package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统验证码
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_sys_captcha")
public class SysCaptchaEntity extends BaseEntity {
	
	private String uuid;
	/**
	 * 验证码
	 */
	private String code;
	/**
	 * 过期时间
	 */
	private Date expireTime;
	
}
