package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
	@NotBlank(message = "缺少路线名称")
	private String name;
	
	/**
	 * 巡检点位ID集合
	 */
	@TableField(exist = false)
	private List<Long> pointIdList;
	
	/**
	 * 巡检点位IDStr集合
	 */
	@TableField(exist = false)
	private List<String> pointIdStrList;
	
	/**
	 * 巡检开始时间
	 */
	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonFormat(pattern = "HH:mm",timezone = "GMT+8")
	private LocalTime startTime;
	
	/**
	 * 巡检结束时间
	 */
	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonFormat(pattern = "HH:mm",timezone = "GMT+8")
	private LocalTime endTime;
	
	/**
	 * 备注
	 */
	private String remark;
	
}
