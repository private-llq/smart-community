package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description APP版本
 * @since 2021-05-31 13:49
 **/
@Data
@TableName("t_app_version")
public class AppVersionEntity extends BaseEntity{
	//id
	@TableId(type = IdType.AUTO)
	private Long id;
	//系统类型 1.安卓 2.IOS
	@NotNull(message = "缺少系统类型")
	@Range(min = 1, max = 2, message = "系统类型错误 1.安卓 2.IOS")
	private Integer sysType;
	//版本号
	@NotBlank(message = "缺少版本号")
	private String sysVersion;
	//是否支持支付
	@Range(min = 0, max = 1, message = "是否支持支付 设置错误 0.不支持 1.支持 默认0")
	private Integer paySupport;
}
