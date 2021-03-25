package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lihao
 * @ClassName ShopDetailsVO
 * @Date 2021/3/23  16:49
 * @Description 店铺更改详情页面数据
 * @Version 1.0
 **/
@Data
@ApiModel("店铺更改详情页面数据")
public class ShopDetailsVO implements Serializable {
	@ApiModelProperty(value = "城市id")
	private Long cityId;
	
	@ApiModelProperty(value = "城市")
	private String city;
	
	@ApiModelProperty(value = "区域id")
	private Long areaId;
	
	@ApiModelProperty(value = "区域")
	private String area;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "商铺(地址)所在社区")
	private String community;
	
	@ApiModelProperty(value = "楼层")
	private String floor;
	
	@ApiModelProperty(value = "所属类型id")
	private Long shopTypeId;
	
	@ApiModelProperty(value = "所属类型")
	private String shopType;
	
	@ApiModelProperty(value = "店铺状态 0空置中 1营业中")
	private Integer status;
	
	@ApiModelProperty(value = "所属行业id")
	private Long shopBusinessId;
	
	@ApiModelProperty(value = "所属行业")
	private String shopBusiness;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	private Double shopAcreage;
	
	@ApiModelProperty(value = "面宽")
	private Double shopWidth;
	
	@ApiModelProperty(value = "进深")
	private Double shopDepth;
	
	@ApiModelProperty(value = "层高")
	private Double shopHeight;
	
	@ApiModelProperty(value = "头图")
	private List<String> headImg;
	
	@ApiModelProperty(value = "室内图")
	private List<String> middleImg;
	
	@ApiModelProperty(value = "其他图")
	private List<String> otherImg;
	
	@ApiModelProperty(value = "押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "月租金")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "转让费")
	private BigDecimal transferMoney;
	
	@ApiModelProperty(value = "起租月数")
	private Integer startLease;
	
	@ApiModelProperty(value = "免租月数")
	private Integer freeLease;
	
	@ApiModelProperty(value = "配套设施集合")
	private List<Long> shopFacilityList;
	
	@ApiModelProperty(value = "配套设施Code")
	private Long shopFacility;
	
	@ApiModelProperty(value = "客流人群集合")
	private List<Long> shopPeoples;
	
	@ApiModelProperty(value = "客流人群Code")
	private Long shopPeople;
	
	@ApiModelProperty(value = "标题")
	private String title;
	
	@ApiModelProperty(value = "概述")
	private String summarize;
	
	@ApiModelProperty(value = "称呼")
	private String nickname;
	
	@ApiModelProperty(value = "手机")
	private String mobile;
	
	@ApiModelProperty(value = "经度")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	private BigDecimal lat;
}
