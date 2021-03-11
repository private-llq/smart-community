package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

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

	@ApiModelProperty("人脸地址")
	private String faceUrl;

	@ApiModelProperty("电话号码")
	private String mobile;
	
	@ApiModelProperty("性别，0未知，1男，2女")
	private Integer sex;

	@ApiModelProperty("生日")
	private LocalDateTime birthdayTime;
	
	@ApiModelProperty("真实姓名")
	private String realName;
	
	@ApiModelProperty("身份证")
	private String idCard;
	
	@ApiModelProperty("是否实名认证")
	private Integer isRealAuth;

	@ApiModelProperty("国籍ID")
	private Integer countryId;

	@ApiModelProperty("省ID")
	private Integer provinceId;
	
	@ApiModelProperty("市ID")
	private Integer cityId;
	
	@ApiModelProperty("区ID")
	private Integer areaId;
	
	@ApiModelProperty("详细地址")
	private String detailAddress;
	
	@ApiModelProperty("离线推送id")
	private String regId;

	@ApiModelProperty("家属关系code")
	@TableField( exist = false )
	private Integer relationCode;

	@ApiModelProperty("证件类型：1.身份证 2.护照")
	private Integer identificationType;
	
	@ApiModelProperty("证件照片(正面)")
	private String idCardPicFace;
	
	@ApiModelProperty("证件照片(反面)")
	private String idCardPicBack;

	@ApiModelProperty("微信")
	private String wechat;

	@ApiModelProperty("腾讯qq")
	private String qq;

	@ApiModelProperty("联系邮箱")
	private String email;

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
