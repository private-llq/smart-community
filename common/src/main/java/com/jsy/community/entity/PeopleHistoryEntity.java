package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author chq459799974
 * @description 访客进出记录实体类
 * @since 2021-04-13 13:45
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_people_history")
public class PeopleHistoryEntity extends BaseEntity{

	// 社区ID
	private Long communityId;

	// 机器ID(序列号)
	private String facesluiceId;

	// 机器名称
	private String facesluiceName;

	// 电话
	private String mobile;

	// 身份证号码
	private String idCard;

	// 机器的人员id
	private Long personId;

	// 用户uid或访客visitorId
	private String uid;

	// 姓名
	private String name;

	// 身份类型
	private String identityType;

	// 认证结果;0:无;1:允许;2:拒绝;3:还没有注册;22:待核验(开门方式为3:人脸核验+远程开门方式的控制记录);24:无权限（特殊版本非通行时间段的控制记录）
	private Integer verifyStatus;

	// 名单类型;0:白名单;1:黑名单
	private Integer personType;

	// 本次进出方式 1.二维码 2.人脸识别 (备用)
	private Integer accessType;

	// 进出方向 1.入方向 2.出方向 (数据来源跟新增设备有关)
	private String direction;

	// 实时检测人脸温度
	private Double temperature;

	// 实时检测人脸温度是否超过阈值,0：没超过；1：超过
	private Integer temperatureAlarm;

	// 是否已同步 0.否 1.是
	private Integer isSync;

	// 批次号
	private Integer version;

	// 分压查询人员进出记录(姓名/电话号码)关键字
	@TableField(exist = false)
	private String searchText;

	// 开门状态;0正常,1失败
	@TableField(exist = false)
	private Integer openStatus;

	// 开门状态字符串
	@TableField(exist = false)
	private String verifyStatusStr;

	// 开门类型字符串
	@TableField(exist = false)
	private String accessTypeStr;
}
