package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
	@NotBlank(groups = {FaceOprationValidate.class}, message = "uid不能为空")
	private String uid;
	
	@ApiModelProperty("业主ID")
	private Long householderId;

	@ApiModelProperty("昵称")
	private String nickname;
	
	@ApiModelProperty("头像地址")
	private String avatarUrl;

	@ApiModelProperty("人脸地址")
	private String faceUrl;

	// 人脸启用状态;1:启用;2:禁用
	private Integer faceEnableStatus;

	@ApiModelProperty("电话号码")
	@NotBlank(groups = {FaceOprationValidate.class}, message = "电话号码不能为空")
	private String mobile;
	
	@ApiModelProperty("性别，0未知，1男，2女")
	private Integer sex;

	@ApiModelProperty("生日")
	private LocalDateTime birthdayTime;
	
	@ApiModelProperty("真实姓名")
	@NotBlank(groups = {FaceOprationValidate.class}, message = "真实姓名不能为空")
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

	// 人脸启用状态;1:启用;2:禁用
	private String faceEnableStatusStr;

	@ApiModelProperty("房屋id")
	@TableField( exist = false)
	private Long houseId;

	@ApiModelProperty("车辆信息")
	@TableField( exist = false )
	private CarEntity carEntity;

	@ApiModelProperty("导入excel记录用户的房屋信息")
	@TableField( exist = false )
	private HouseEntity houseEntity;
	
	@ApiModelProperty("用户聊天ID")
	@TableField( exist = false )
	private String imId;

	@ApiModelProperty("家属关系code")
	@TableField( exist = false )
	private Integer relationCode;

	// 与业主关系 1.业主 6.亲属，7租户
	@TableField( exist = false )
	private Set<String> relationSet;

	// (搜索用)关键字
	@TableField( exist = false )
	private String keyword;

	// 下发状态;1:失败(未完整同步);2;成功
	@TableField( exist = false )
	private Integer distributionStatus;


	// 下发状态;1:失败(未完整同步);2;成功
	@TableField( exist = false )
	private String distributionStatusStr;

	// 社区ID
	@TableField( exist = false )
	private Long communityId;

	// 设备ID
	@TableField( exist = false )
	@NotEmpty(groups = {FaceOprationValidate.class}, message = "设备列表不能为空")
	private List<String> hardwareId;

	/**
	 * 业主登记验证接口
	 */
	public interface ProprietorRegister{}

	/**
	 * 用户人脸操作验证组
	 */
	public interface FaceOprationValidate{}

}
