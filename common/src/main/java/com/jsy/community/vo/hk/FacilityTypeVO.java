package com.jsy.community.vo.hk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author lihao
 * @ClassName FacilityTypeVO
 * @Date 2021/3/12  14:22
 * @Description 设备类别树形结构展示
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FacilityTypeVO对象", description = "设备类别树形结构展示")
public class FacilityTypeVO implements Serializable {
	
	@ApiModelProperty(value = "设备类别id")
	private Long id;
	
	@ApiModelProperty(value = "父id")
	private Long pid;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "类别名称")
	private String name;
	
	@ApiModelProperty(value = "设备数量")
	private int count;
	
	@ApiModelProperty(value = "子类别")
	private List<FacilityTypeVO> children;
	
}
