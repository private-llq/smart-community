package com.jsy.community.qo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author 91李寻欢
 * @ClassName CarTrackQO
 * @Date 2021/4/25  14:05
 * @Description TODO
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CarTrackQO查询对象", description = "用于物业车辆轨迹分页搜索")
public class CarTrackQO extends BaseQO{
	
	@ApiModelProperty(value = "社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "车牌号")
	private String carNumber;
	
	@ApiModelProperty(value = "车牌颜色")
	private String color;
	
	@ApiModelProperty(value = "设备编号/名称/位置")
	private String facilitySearch;
	
	@ApiModelProperty(value = "认证类型 0 未认证 1 已认证")
	private Integer authType;
	
	@ApiModelProperty(value = "开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date endTime;
}
