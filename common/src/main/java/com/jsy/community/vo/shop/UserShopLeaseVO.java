package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author lihao
 * @ClassName UserShopLeaseVO
 * @Date 2021/3/25  10:43
 * @Description TODO
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserShopLeaseVO对象", description="业主发布店铺列表展示")
public class UserShopLeaseVO implements Serializable {
	@ApiModelProperty(value = "商铺id")
	private Long Id;
	
	@ApiModelProperty(value = "商铺地址")
	private String address;
	
	@ApiModelProperty(value = "商铺状态")
	private String statusString;
	
	@ApiModelProperty(value = "商铺所属类型")
	private String shopType;
	
	@ApiModelProperty(value = "押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "月租金")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "商铺展示地址")
	private String shopShowImg;

	@ApiModelProperty(value = "标题")
	private String title;
}
