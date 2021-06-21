package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lihao
 * @ClassName IndexShopVO
 * @Date 2020/12/30  9:44
 * @Description 用于列表展示
 * @Version 1.0
 **/
@Data
@ApiModel("商铺列表查询")
public class IndexShopVO implements Serializable {
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	@ApiModelProperty(value = "业主id")
	private String uid;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "标题")
	private String title;
	
	@ApiModelProperty(value = "概述")
	private String summarize;
	
	@ApiModelProperty(value = "月租金")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	private Double shopAcreage;
	
	@ApiModelProperty(value = "店铺图片")
	private String imgPath;
	
	@ApiModelProperty(value = "标签集合")
	private List<String> tags;
	
	@ApiModelProperty(value = "店铺地址")
	private String address;
	
	@ApiModelProperty(value = "月租金  字符串形式")
	private String monthMoneyString;

	@ApiModelProperty(value = "房源类型")
	private String defrayType;

}
