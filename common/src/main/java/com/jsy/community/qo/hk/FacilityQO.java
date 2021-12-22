package com.jsy.community.qo.hk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author lihao
 * @ClassName FacilityQO
 * @Date 2021/3/13  16:10
 * @Description
 * @Version 1.0
 **/
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FacilityQO查询对象", description = "用于设备查询条件")
public class FacilityQO extends BaseQO {
	
	@ApiModelProperty(value = "设备分类")
	private String facilityTypeId;
	
	@ApiModelProperty(value = "设备编号")
	private String searchText;
	
	@ApiModelProperty(value = "是否只展示需要同步数据 0 不是  1是 ")
	private Integer isConnectData;
	
	@ApiModelProperty(value = "数据同步开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date startTime;
	
	@ApiModelProperty(value = "数据同步结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endTime;
	
	@ApiModelProperty(value = "设备状态(不限/在线/不在线  0/1/2)")
	private Integer status;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
}
