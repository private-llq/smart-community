package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author jsy
 * @since 2020-12-17
 */
@Data
@ApiModel("店铺")
public class ShopLeaseVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "业主id")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "业主id不能为空")
	private String uid;
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "社区id不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "房屋id")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "房屋id不能为空")
	private Long houseId;
	
	@ApiModelProperty(value = "标题")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "标题不能为空")
	private String title;
	
	@ApiModelProperty(value = "概述")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "商铺概述不能为空")
	private String summarize;
	
	@ApiModelProperty(value = "月租金 0.00：面议")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "月租金不能为空")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "押付方式")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "押付方式不能为空")
	private String defrayType;
	
	@ApiModelProperty(value = "转让费 0.00：面议 ")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "转让费不能为空")
	private BigDecimal transaferMoney;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "店铺面积不能为空")
	private Integer shopAcreage;
	
	@ApiModelProperty(value = "房型")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "房型不能为空")
	private String type;
	
	@ApiModelProperty(value = "楼层")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "楼层不能为空")
	private Integer floor;
	
	@ApiModelProperty(value = "楼层总数")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "楼层总数不能为空")
	private Integer floorCount;
	
	@ApiModelProperty(value = "朝向   0东 1南 2西 3 北 4东南 5 东北 6 西南 7 西北 8 南北 9东西")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "朝向不能为空")
	private Integer orientation;
	
	@ApiModelProperty(value = "电梯   0无 1有")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "电梯不能为空")
	private Integer lift;
	
	@ApiModelProperty(value = "装修程度  0毛坯房 1普通装修  2精装修")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "装修程度不能为空")
	private Integer furnishingStyle;
	
	@ApiModelProperty(value = "年代")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "年代不能为空")
	private String year;
	
	@ApiModelProperty(value = "用途 0普通住宅 1商业住宅")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "用途不能为空")
	private Integer purpose;
	
	@ApiModelProperty(value = "权属 0商品房 1学区房 2家住房")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "权属不能为空")
	private Integer ownership;
	
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
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "经度不能为空")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "纬度不能为空")
	private BigDecimal lat;
	
	@ApiModelProperty(value = "来源 0个人 1业主 2不限")
	private Integer source;
	
	@ApiModelProperty(value = "店铺地址")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "店铺地址不能为空")
	private String address;
	
	@ApiModelProperty(value = "月租金  字符串形式")
	private String MonthMoneyString;
	
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
