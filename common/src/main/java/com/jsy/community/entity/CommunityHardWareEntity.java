package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 社区硬件实体
 * @since 2021-04-25 13:41
 **/
@TableName("t_community_hardware")
public class CommunityHardWareEntity implements Serializable {
	private Long communityId;//社区ID
	private Integer hardwareType;//硬件类型 1.炫优人脸识别一体机
	private String hardwareId;//硬件id
	//设备名称
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
}
