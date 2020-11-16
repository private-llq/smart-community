package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 轮播图返回前端
 * @Author
 * @Date 2020/11/16 14:16
 **/
@Data
@ApiModel("轮播图查询返回")
public class BannerVO implements Serializable {
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "文件路径")
	private String url;
	
	@ApiModelProperty(value = "描述")
	private String description;
	
	@ApiModelProperty(value = "轮播排序12345")
	private Integer sort;
	
	@ApiModelProperty(value = "Banner位置1.顶部 2.底部")
	private Integer position;
}
