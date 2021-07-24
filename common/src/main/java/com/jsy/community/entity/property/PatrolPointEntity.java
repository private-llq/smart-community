package com.jsy.community.entity.property;

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
	@NotBlank(message = "巡更点名称不能为空")
	private String name;
	
	/**
	 * 点位编号(钮号)
	 */
	@NotBlank(message = "设备编号(钮号)不能为空")
	private String number;
	
	/**
	 * 所属楼栋
	 */
	private Long buildingId;
	private String buildingName;
	
	/**
	 * 所属单元
	 */
	private Long unitId;
	private String unitName;
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 经度
	 */
	@NotNull(message = "经度不能为空")
	private Double lon;
	
	/**
	 * 纬度
	 */
	@NotNull(message = "纬度不能为空")
	private Double lat;
	
	/**
	 * 备注
	 */
	private String remark;
	
}
