package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_auth")
public class UserAuthEntity extends BaseEntity {
	private Long uid;
	private String username;
	private String email;
	private String mobile;
	private String password;
	private String salt;
}
