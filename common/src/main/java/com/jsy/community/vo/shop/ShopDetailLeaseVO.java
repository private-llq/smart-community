package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lihao
 * @ClassName ShopDetailLeaseVO
 * @Date 2021/1/7  18:05
 * @Description TODO
 * @Version 1.0
 **/
@Data
@ApiModel("店铺详情")
public class ShopDetailLeaseVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "业主id")
	private String uid;
	
	@ApiModelProperty(value = "社区id")
	private Long communityId;
	
	@ApiModelProperty(value = "房屋id")
	private Long houseId;
	
	@ApiModelProperty(value = "标题")
	private String title;
	
	@ApiModelProperty(value = "概述")
	private String summarize;
	
	@ApiModelProperty(value = "月租金 0.00：面议")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "转让费 0.00：面议 ")
	private BigDecimal transaferMoney;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	private Integer shopAcreage;
	
	@ApiModelProperty(value = "房型")
	private String type;
	
	@ApiModelProperty(value = "楼层")
	private Integer floor;
	
	@ApiModelProperty(value = "楼层总数")
	private Integer floorCount;
	
	@ApiModelProperty(value = "朝向   0东 1南 2西 3 北 4东南 5 东北 6 西南 7 西北 8 南北 9东西")
	private String orientation;
	
	@ApiModelProperty(value = "电梯   0无 1有")
	private String lift;
	
	@ApiModelProperty(value = "装修程度  0毛坯房 1普通装修  2精装修")
	private String furnishingStyle;
	
	@ApiModelProperty(value = "年代")
	private String year;
	
	@ApiModelProperty(value = "用途 0普通住宅 1商业住宅")
	private String purpose;
	
	@ApiModelProperty(value = "权属 0商品房 1学区房 2家住房")
	private String ownership;
	
	@ApiModelProperty(value = "图片地址集合")
	private String[] imgPath;
	
	@ApiModelProperty(value = "商铺类型id集合")
	private Long[] shopTypeIds;
	
	@ApiModelProperty(value = "商铺类型名称集合")
	private List<String> shopTypeNames;
	
	@ApiModelProperty(value = "商铺行业id集合")
	private Long[] shopBusinessIds;
	
	@ApiModelProperty(value = "商铺行业名称集合")
	private List<String> shopBusinessNames;
	
	@ApiModelProperty(value = "经度")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	private BigDecimal lat;
	
	@ApiModelProperty(value = "来源 0个人 1业主 2不限")
	private Integer source;
	
	@ApiModelProperty(value = "店铺地址")
	private String address;
	
	@ApiModelProperty(value = "月租金  字符串形式")
	private String monthMoneyString;
	
	@ApiModelProperty(value = "转让费  字符串形式")
	private String transferMoneyString;
	
	@ApiModelProperty(value = "详情页总标签")
	private List<String> tags;
	
	/**
	 * 添加店铺接口
	 */
	public interface addShopValidate {
	}
	
	/**
	 * 修改店铺接口
	 */
	public interface updateShopValidate {
	}
}
