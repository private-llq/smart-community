package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description APP版本
 * @since 2021-05-31 13:49
 **/
@Data
@TableName("t_app_version")
public class AppVersionEntity implements Serializable {
	//id
	private Long id;
	//系统类型 1.安卓 2.IOS
	private Integer sysType;
	//版本号
	private String sysVersion;
	//是否支持支付 0.不支持 1.支持
	private Integer paySupport;
	//创建时间
	private LocalDateTime createTime;
}
