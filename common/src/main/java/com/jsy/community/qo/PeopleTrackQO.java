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
 * @ClassName PeopleTrackQO
 * @Date 2021/4/29  9:33
 * @Description 人员轨迹查询
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PeopleTrackQO查询对象", description = "用于人员轨迹分页搜索")
public class PeopleTrackQO extends BaseQO{
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "人员姓名")
	private String peopleName;
	
	@ApiModelProperty(value = "搜索条件")
	private String searchText;
	
	@ApiModelProperty(value = "认证类型 0 未认证 1 已认证")
	private Integer authType;
	
	@ApiModelProperty(value = "拍摄开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date startTime;
	
	@ApiModelProperty(value = "拍摄结束日期")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date endTime;
}
