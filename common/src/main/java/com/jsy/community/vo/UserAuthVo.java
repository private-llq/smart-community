package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ling
 * @since 2020-11-12 16:50
 */
@Data
@ApiModel("业主登录")
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthVo implements Serializable {
	@ApiModelProperty("token")
	private String token;
	
	@ApiModelProperty("过期时间")
	private LocalDateTime expiredTime;
	
	@ApiModelProperty("业主个人信息")
	private UserInfoVo userInfo;
}
