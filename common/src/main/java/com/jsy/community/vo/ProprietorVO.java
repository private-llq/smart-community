package com.jsy.community.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *  物业端VO业主查询信息返回实体类
 * @author YuLF
 * @since  2020/11/30 11:29
 */
@Data
@ApiModel("业主信息查询信息返回")
public class ProprietorVO extends BaseVO {

	@ApiModelProperty("业主ID")
	private Long householderId;

	@ApiModelProperty("昵称")
	private String nickname;

	@ApiModelProperty("头像地址")
	private String avatarUrl;

	@ApiModelProperty("电话号码")
	private String mobile;

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

	@ApiModelProperty("省名")
	private String provinceName;

	@ApiModelProperty("市名")
	private String cityName;

	@ApiModelProperty("区名")
	private String areaName;

}
