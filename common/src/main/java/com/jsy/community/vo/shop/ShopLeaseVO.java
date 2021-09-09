package com.jsy.community.vo.shop;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
@Data
@ApiModel("店铺详情")
public class ShopLeaseVO implements Serializable {
	
//	@ApiModelProperty(value = "业主id")
//	private String uid;
//
//	@ApiModelProperty(value = "城市id")
//	private Long cityId;
//
//	@ApiModelProperty(value = "区域id")
//	private Long areaId;
//
//	@ApiModelProperty(value = "社区id")
//	private Long communityId;
//
	@ApiModelProperty(value = "商铺id")
	private Long Id;
	
	@ApiModelProperty(value = "标题")
	private String title;
	
	@ApiModelProperty(value = "概述")
	private String summarize;
	
	@ApiModelProperty(value = "月租金 0.00：面议")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "转让费 0.00：面议 ")
	private BigDecimal transferMoney;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	private Double shopAcreage;
	
	@ApiModelProperty(value = "面宽")
	private Double shopWidth;
	
	@ApiModelProperty(value = "进深")
	private Double shopDepth;
	
	@ApiModelProperty(value = "层高")
	private Double shopHeight;
	
	@ApiModelProperty(value = "楼层")
	private String floor;
	
	@ApiModelProperty(value = "图片地址集合")
	private String[] imgPath;
	
	@ApiModelProperty(value = "经度")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	private BigDecimal lat;
	
//	@ApiModelProperty(value = "来源 0个人 1物业")
//	private Integer source;
	
	@ApiModelProperty(value = "来源 0个人 1物业")
	private String sourceString;
	
	@ApiModelProperty(value = "月租金  字符串形式")
	private String monthMoneyString;
	
	@ApiModelProperty(value = "转让费  字符串形式")
	private String transferMoneyString;
	
	@ApiModelProperty(value = "详情页总标签")
	private List<String> tags;
	
//	@ApiModelProperty(value = "手机")
//	private String mobile;
	
	@ApiModelProperty(value = "免租月数")
	private Integer freeLease;
	
	@ApiModelProperty(value = "起租月数")
	private Integer startLease;
	
//	@ApiModelProperty(value = "店铺状态 0空置中 1经营中")
//	private Integer status;
	
	@ApiModelProperty(value = "店铺状态 0空置中 1经营中  字符串形式")
	private String statusString;
	
//	@ApiModelProperty(value = "称呼")
//	private String nickname;
	
//	@ApiModelProperty(value = "头像")
//	private String userImg;
	
	
	
	
//	@ApiModelProperty(value = "客流人群Code")
//	private List<Long> shopPeoples;
	
	@ApiModelProperty(value = "客流人群Code")
	private List<String> shopPeopleStrings;
	
	@ApiModelProperty(value = "配套设施Code")
	private Long shopFacility;
	
	@ApiModelProperty(value = "配套设施名称")
	private List<String> shopFacilityStrings;
	
//	@ApiModelProperty(value = "所属商业id")
//	private Long shopBusinessId;
	
	@ApiModelProperty(value = "所属商业字符串")
	private String shopBusinessString;
	
//	@ApiModelProperty(value = "所属类型id")
//	private Long shopTypeId;
	
	@ApiModelProperty(value = "所属类型字符串")
	private String shopTypeString;
	
	@ApiModelProperty(value = "商铺地址")
	private String shopAddress;

	// 签约操作状态;0;没有签约;1:发起签约;2:接受申请;3:拟定合同;4:等待支付房租;5:支付完成;6:完成签约;7:取消申请;8:拒绝申请;9:重新发起
	@TableField(exist = false)
	private Integer operation;

	// 签约ID
	@TableField(exist = false)
	private String contractId;
}
