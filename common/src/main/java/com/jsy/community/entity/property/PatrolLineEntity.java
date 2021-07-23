package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 巡检线路实体
 * @since 2021-07-23 15:28
 **/
@Data
@TableName("t_patrol_line")
public class PatrolLineEntity extends BaseEntity {
	
	/**
	 * 社区ID
	 */
	private Long communityId;
	
	/**
	 * 硬件品牌ID
	 */
	private Long brandId;
	
	/**
	 * 巡检线路名称
	 */
	private String name;
	
	/**
	 * 巡检开始时间
	 */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private LocalDateTime startTime;
	
	/**
	 * 巡检结束时间
	 */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private LocalDateTime endTime;
	
	/**
	 * 备注
	 */
	private String remark;
	
}
