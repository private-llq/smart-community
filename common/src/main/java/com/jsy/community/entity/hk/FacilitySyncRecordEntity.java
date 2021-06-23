package com.jsy.community.entity.hk;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 设备数据同步记录
 * @since 2021-06-23 09:59
 **/
@Data
@TableName("t_facility_sync_record")
@ApiModel(value="FacilitySyncRecordEntity对象", description="设备数据同步记录")
public class FacilitySyncRecordEntity extends BaseEntity {
	
	@ApiModelProperty(value = "设备id")
	private Long facility_id;
	
	@ApiModelProperty(value = "设备编号")
	private String number;
	
	@ApiModelProperty(value = "是否同步成功 0.失败 1.成功")
	private Integer isSuccess;
	
}
