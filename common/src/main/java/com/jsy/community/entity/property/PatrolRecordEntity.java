package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 巡检记录实体
 * @since 2021-07-26 15:58
 **/
@Data
@TableName("t_patrol_record")
public class PatrolRecordEntity implements Serializable {
	
	/**
	 * ID
	 */
	private Long id;
	
	/**
	 * 社区ID
	 */
	private Long communityId;
	
	/**
	 * 点位编号(钮号)
	 */
	private String pointNumber;
	
	/**
	 * 点位名称
	 */
	private String pointName;
	
	/**
	 * 点位地址
	 */
	private String pointAddress;
	
	/**
	 * 打卡时间
	 */
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private LocalDateTime patrolTime;
	
	/**
	 * 查询条件 开始时间
	 */
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	@TableField(exist = false)
	private LocalDate startTime;
	
	/**
	 * 查询条件 结束时间
	 */
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	@TableField(exist = false)
	private LocalDate endTime;
	
	/**
	 * 设备编号
	 */
	private String equipNumber;
	
	/**
	 * 设备名称
	 */
	private String equipName;
	
}
