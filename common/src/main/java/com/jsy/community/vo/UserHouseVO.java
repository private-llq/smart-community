package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lihao
 * @ClassName UserHouseVO
 * @Date 2020/11/25  16:11
 * @Description 物业端业主房屋认证审核信息
 * @Version 1.0
 **/
@Data
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserHouseVO implements Serializable {
	
	@ApiModelProperty(value = "房屋id")
	private Long id;
	
	@ApiModelProperty(value = "业主")
	private String nickname;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "所属社区")
	private String name;
	
	@ApiModelProperty(value = "门牌ID")
	private Long houseId;
	
	@ApiModelProperty(value = "楼栋名")
	private String building;
	
	@ApiModelProperty(value = "单元名")
	private String unit;
	
	@ApiModelProperty(value = "楼层名")
	private String floor;
	
	@ApiModelProperty(value = "门牌名")
	private String door;
	
	@ApiModelProperty(value = "门牌唯一标识")
	private String code;
	
	@ApiModelProperty(value = "是否通过审核")
	private Integer checkStatus;
	
	@ApiModelProperty(value = "是否通过审核")
	private String checkStatusString;

}
