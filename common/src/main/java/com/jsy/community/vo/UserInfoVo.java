package com.jsy.community.vo;

import com.jsy.community.entity.CarEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 业主个人信息
 *
 * @author ling
 * @since 2020-11-12 16:51
 */
@Data
@ApiModel("业主个人信息")
public class UserInfoVo implements Serializable {
	// 主键id
	private Long id;
	
	@ApiModelProperty("ID")
	private Long thirdPlatformId;

	@ApiModelProperty("是否绑定手机1已绑定，0未绑定")
	private Integer isBindMobile;

	@ApiModelProperty("是否绑定微信1已绑定，0未绑定")
	private Integer isBindWechat;

	@ApiModelProperty("是否绑定支付宝1已绑定，0未绑定")
	private Integer isBindAlipay;

	@ApiModelProperty("是否设置支付密码1已绑定，0未绑定")
	private Integer isBindPayPassword;

	@ApiModelProperty("是否设置登录密码1已绑定，0未绑定")
	private Integer isBindPassword;

	@ApiModelProperty("手机号")
	private String mobile;

	@ApiModelProperty("业主ID")
	private String uid;

	@ApiModelProperty("房屋ID")
	private Long houseId;

	@ApiModelProperty("业主社区ID")
	private String communityId;

	@ApiModelProperty(value = "极光推送全部标签")
	private String uroraTags;
	
	@ApiModelProperty(value = "用户imID")
	private String imId;

	@ApiModelProperty(value = "用户im密码")
	private String imPassword;
	
	@ApiModelProperty("昵称")
	private String nickname;
	
	@ApiModelProperty("头像地址")
	private String avatarUrl;
	
	@ApiModelProperty("性别，0未知，1男，2女")
	private Integer sex;
	
	@ApiModelProperty("真实姓名")
	private String realName;

	/**
	 * 生日
	 */
	private String birthday;
	
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

	@ApiModelProperty("业主家属")
	private List<RelationVO> proprietorMembers;

	@ApiModelProperty("业主房屋")
	private List<HouseVo> proprietorHouses;

	@ApiModelProperty("业主车辆")
	private List<CarEntity> proprietorCars;

}
