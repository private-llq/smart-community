package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author chq459799974
 * @description 巡检点实体
 * @since 2021-07-23 15:28
 **/
@Data
@TableName("t_patrol_point")
public class PatrolPointEntity extends BaseEntity {
	
	/**
	 * 社区ID
	 */
	private Long communityId;
	
	/**
	 * 硬件品牌ID
	 */
	private Long brandId;
	
	/**
	 * 巡检点位名称
	 */
	@NotBlank(message = "缺少巡更点名称")
	private String name;
	
	/**
	 * 点位编号(钮号)
	 */
	@NotBlank(message = "缺少设备编号(钮号)")
	private String number;
	
	/**
	 * 所属楼栋
	 */
	private Long buildingId;
	@TableField(exist = false)
	private String buildingIdStr;
	public String getBuildingIdStr(){
		return String.valueOf(buildingId);
	}
	@TableField(exist = false)
	private String buildingName;
	
	/**
	 * 所属单元
	 */
	private Long unitId;
	@TableField(exist = false)
	private String unitIdStr;
	public String getUnitIdStr(){
		return String.valueOf(unitId);
	}
	@TableField(exist = false)
	private String unitName;
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 经度
	 */
	@NotNull(message = "缺少经度")
	private Double lon;
	
	/**
	 * 纬度
	 */
	@NotNull(message = "缺少纬度")
	private Double lat;
	
	/**
	 * 备注
	 */
	private String remark;
	
}
