package com.jsy.community.qo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lihao
 * @ClassName RepairOrderQO
 * @Date 2021/3/15  16:51
 * @Description 物业端报修单查询对象
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RepairOrderVO对象", description = "物业端报修单查询对象")
public class RepairOrderQO implements Serializable {
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "0 待处理 1 修复中 2 已完成  3 驳回")
	private Integer status;
	
	@ApiModelProperty(value = "报修类别 0 个人报修 1 公共报修")
	private Integer repairType;
	
	@ApiModelProperty(value = "报修人/电话/编号")
	private String searchText;
	
	@ApiModelProperty(value = "报修内容")
	private String problem;
	
	@ApiModelProperty(value = "报修事项id")
	private Long type;
	
	@ApiModelProperty(value = "报修事项")
	private String typeName;
	
	@ApiModelProperty(value = "报修查询开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date startTime;
	
	@ApiModelProperty(value = "报修结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date endTime;
	
	@ApiModelProperty(value = "评价类型 评价类型 0 好评  2 中评")
	private Integer commentStatus;
	
}
