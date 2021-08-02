package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 陌生访客实体类
 * @since 2021-08-02 13:58
 **/
@Data
@TableName("t_visitor_stranger")
public class VisitorStrangerEntity {
	
	/**
	 * ID
	 */
	@TableId(type = IdType.NONE)
	private Long id;
	
	/**
	 * 社区ID
	 */
	private Long communityId;
	
	/**
	 * 抓拍时间
	 */
	private LocalDateTime snapTime;
	
	/**
	 * 抓拍图片url
	 */
	private String snapUrl;
	
	/**
	 * 设备ID
	 */
	private String machineId;
	
	/**
	 * 设备名称
	 */
	private String machineName;
	
}
