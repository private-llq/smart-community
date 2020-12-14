package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


/**
 * 后台系统用户Token
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_sys_user_token")
public class SysUserTokenEntity extends BaseEntity {
	
	//用户ID
//	@TableId(type = IdType.INPUT)
	private Long userId;
	//token
	private String token;
	//过期时间
	private LocalDateTime expireTime;
	
}
