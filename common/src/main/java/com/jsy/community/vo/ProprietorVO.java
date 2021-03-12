package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  物业端VO业主查询信息返回实体类
 * @author YuLF
 * @since  2020/11/30 11:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("业主信息查询信息返回")
public class ProprietorVO extends BaseVO {

	@ApiModelProperty("用户ID")
	private String uid;

	@ApiModelProperty("业主ID")
	private Long householderId;

	@ApiModelProperty("房屋ID")
	private Long houseId;

	@ApiModelProperty("昵称")
	private String nickname;

	@ApiModelProperty("头像地址")
	private String avatarUrl;

	@ApiModelProperty("电话号码")
	private String mobile;

	@ApiModelProperty("性别，0未知，1男，2女")
	private Integer sex;

	@ApiModelProperty("性别，未知，男，女")
	private String gender;

	@ApiModelProperty("年龄")
	private String age;

	@ApiModelProperty("真实姓名")
	private String realName;

	@ApiModelProperty("身份证")
	private String idCard;

	@ApiModelProperty("微信")
	private String wechat;

	@ApiModelProperty("腾讯qq")
	private String qq;

	@ApiModelProperty("联系邮箱")
	private String email;

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

	@ApiModelProperty("创建人 / 创建时间")
	private String createDate;

	@ApiModelProperty("最近更新人 / 最近更新时间")
	private String updateDate;

	@ApiModelProperty("房屋合并后的字符串")
	private String houseMergeName;

}
