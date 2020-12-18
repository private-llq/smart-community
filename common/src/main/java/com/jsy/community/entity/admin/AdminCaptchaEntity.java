package com.jsy.community.entity.admin;

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
@TableName("t_admin_captcha")
public class AdminCaptchaEntity implements Serializable {
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
