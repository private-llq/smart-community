package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
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
	private LocalDateTime patrolTime;
	
	/**
	 * 设备编号
	 */
	private String equipNumber;
	
	/**
	 * 设备名称
	 */
	private String equipName;
	
}
