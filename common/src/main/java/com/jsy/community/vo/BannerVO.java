package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 轮播图查询返回类
 * @Author qq459799974
 * @Date 2020/11/16 14:16
 **/
@Data
@ApiModel("轮播图查询返回")
public class BannerVO extends BaseVO {
	
	@ApiModelProperty(value = "文件路径")
	private String url;
	
	@ApiModelProperty(value = "描述")
	private String desc;
	
	@ApiModelProperty(value = "轮播排序12345")
	private Integer sort;
	
	@ApiModelProperty(value = "Banner位置1.顶部 2.底部")
	private Integer position;
	
	@ApiModelProperty(value = "轮播图类型1.非广告 2.广告")
	private Integer type;
	
	@ApiModelProperty(value = "点击量")
	private Integer click;
	
	@ApiModelProperty(value = "内容")
	private String content;
	
}
