package com.jsy.community.qo.hk;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lihao
 * @ClassName FacilityQO
 * @Date 2021/3/13  16:10
 * @Description TODO
 * @Version 1.0
 **/
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="FacilityQO查询对象", description="用于设备查询条件")
public class FacilityQO implements Serializable {
	
	@ApiModelProperty(value = "设备编号")
	private String number;
	
	@ApiModelProperty(value = "设备名称")
	private String name;
	
	@ApiModelProperty(value = "设备分类")
	private String facilityTypeName;
	
	@ApiModelProperty(value = "设备分类id")
	private Long facilityTypeId;
	
	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "数据同步开始时间")
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date startTime;
	
	@ApiModelProperty(value = "数据同步结束时间")
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date endTime;
	
	@ApiModelProperty(value = "设备状态(不限/在线/不在线  0/1/2)")
	private int status;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty("分页查询当前页")
	private Long page;
	
	@ApiModelProperty("分页查询每页数据条数")
	private Long size;
}
