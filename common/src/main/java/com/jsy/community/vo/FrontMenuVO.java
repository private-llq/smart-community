package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author lihao
 * @ClassName FrontMenuVO
 * @Date 2020/11/15  15:10
 * @Description TODO
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("前台菜单信息")
public class FrontMenuVO implements Serializable {
	
	@ApiModelProperty(value = "菜单名")
	private String menuName;
	
	@ApiModelProperty(value = "图标地址")
	private String icon;
	
	@ApiModelProperty(value = "路径地址")
	private String path;
	
	@ApiModelProperty(value = "描述信息")
	private String descr;
	
	@ApiModelProperty(value = "优先级")
	private Integer sort;
	
	@ApiModelProperty(value = "父菜单名")
	private String parentName;
	
	@ApiModelProperty(value = "父id")
	private Long parentId;
}
