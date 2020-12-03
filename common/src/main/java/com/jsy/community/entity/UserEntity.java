package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author ling
 * @since 2020-11-11 17:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("业主实体类")
@TableName("t_user")
public class UserEntity extends BaseEntity {
	
	@ApiModelProperty("uid")
	private String uid;
	
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
	@NotBlank(groups = {ProprietorRegister.class}, message = "姓名未填写!")
	@Pattern(groups = {ProprietorRegister.class}, regexp = RegexUtils.REGEX_REAL_NAME, message = "请输入一个正确的姓名")
	private String realName;
	
	@ApiModelProperty("身份证")
	@NotBlank(groups = {ProprietorRegister.class}, message = "身份证号码未输入!")
	@Pattern(groups = {ProprietorRegister.class}, regexp = RegexUtils.REGEX_ID_CARD, message = "请输入一个正确的身份证号码!")
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

	@ApiModelProperty("车辆信息")
	@TableField( exist = false )
	private CarEntity carEntity;


	@ApiModelProperty("导入excel记录用户的房屋信息")
	@TableField( exist = false )
	private HouseEntity houseEntity;

	/**
	 * 业主登记验证接口
	 */
	public interface ProprietorRegister{}

}
