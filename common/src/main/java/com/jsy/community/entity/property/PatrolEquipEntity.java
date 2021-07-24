package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 巡检设备实体
 * @since 2021-07-23 15:28
 **/
@Data
@TableName("t_patrol_equip")
public class PatrolEquipEntity extends BaseEntity {
	
	/**
	 * 社区ID
	 */
	private Long communityId;
	
	/**
	 * 硬件品牌ID
	 */
	private Long brandId;
	
	/**
	 * 设备编号
	 */
	@NotBlank(message = "设备编号不能为空")
	private String number;
	
	/**
	 * 设备名称
	 */
	@NotBlank(message = "设备名称不能为空")
	private String name;
	
}
