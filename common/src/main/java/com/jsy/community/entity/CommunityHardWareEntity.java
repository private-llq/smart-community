package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 社区硬件实体
 * @since 2021-04-25 13:41
 **/
@Data
@TableName("t_community_hardware")
public class CommunityHardWareEntity extends BaseEntity {
	//硬件id(设备序列号)
	@NotBlank(groups = {addHardWareValidate.class}, message = "硬件id(社保序列号)必须填写")
	private String hardwareId;
	//硬件类型 1.炫优人脸识别一体机
	private Integer hardwareType;
	// 在线状态;1:在线;2:离线
	private Integer onlineStatus;
	//社区ID
	private Long communityId;
	// 楼栋/单元ID
	private Long buildingId;
	//设备名称
	@Length(min = 2, max = 25, message = "设备名称长度2-25位")
	@NotBlank(groups = {addHardWareValidate.class, updateHardWareValidate.class}, message = "设备名称必须填写")
	private String name;
	//IP地址
	private String ip;
	//端口号
	private Integer port;
	//设备用户名
	private String username;
	//设备用户密码
	private String password;
	//设备型号
	private String modelNumber;
	//同步状态;1:已同步;2:未同步
	private Integer isConnectData;
	//同步时间
	private LocalDateTime dataConnectTime;
	//备注
	private String remake;

	// 搜索关键词
	@TableField(exist = false)
	private String searchText;

	// 在线状态字符串
	@TableField(exist = false)
	private String onlineStatusStr;

	// 物业端添加扫描设备(扫脸机)验证组
	public interface addHardWareValidate{}

	// 物业端更新扫描设备(扫脸机)验证组
	public interface updateHardWareValidate{}
}