package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ling
 * @since 2020-11-11 17:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("业主实体类")
@TableName("t_user")
public class UserEntity extends BaseEntity {
	
	@ApiModelProperty("业主ID")
	private Long householderId;
	
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
	
	@ApiModelProperty("省ID")
	private Integer provinceId;
	
	@ApiModelProperty("市ID")
	private Integer cityId;
	
	@ApiModelProperty("区ID")
	private Integer areaId;
	
	@ApiModelProperty("详细地址")
	private String detailAddress;
}
