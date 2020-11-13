package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 业主个人信息
 *
 * @author ling
 * @since 2020-11-12 16:51
 */
@Data
@ApiModel("业主个人信息")
public class UserInfoVo implements Serializable {
	@ApiModelProperty("业主ID")
	private Long id;
	
	@ApiModelProperty("昵称")
	private String nickname;
	
	@ApiModelProperty("头像地址")
	private String avatarUrl;
	
	@ApiModelProperty("性别，0未知，1男，2女")
	private Integer sex;
	
	@ApiModelProperty("真实姓名")
	private String realName;
	
	@ApiModelProperty("身份证")
	private String idCard;
	
	@ApiModelProperty("是否实名认证")
	private Integer isRealAuth;
	
	@ApiModelProperty("省")
	private String province;
	
	@ApiModelProperty("市")
	private String city;
	
	@ApiModelProperty("区")
	private String area;
	
	@ApiModelProperty("详细地址")
	private String detailAddress;
}
