package com.jsy.community.entity.sys;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 后台系统验证码
 */
@Data
@TableName("t_sys_captcha")
public class SysCaptchaEntity implements Serializable {
	@TableId
	private Long id;
	
	private String uuid;
	/**
	 * 验证码
	 */
	private String code;
	/**
	 * 过期时间
	 */
	private Date expireTime;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	
}
