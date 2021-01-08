package com.jsy.community.vo.shop;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
	@Pattern(groups = {addShopValidate.class, updateShopValidate.class},regexp = "^押([0-9]{1,2})付([0-9]{1,2})$",message = "请选择正确的押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "转让费 0.00：面议 ")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "转让费不能为空")
	private BigDecimal transaferMoney;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "店铺面积不能为空")
	private Integer shopAcreage;
	
	@ApiModelProperty(value = "房型")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "房型不能为空")
	@Pattern(groups = {addShopValidate.class, updateShopValidate.class},regexp = "^([0-9]{1,2})室([0-9]{1,2})厅$",message = "请选择正确的房型")
	private String type;
	
	@ApiModelProperty(value = "楼层")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "楼层不能为空")
	private Integer floor;
	
	@ApiModelProperty(value = "楼层总数")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "楼层总数不能为空")
	private Integer floorCount;
	
	@ApiModelProperty(value = "房屋朝向1.东.2.西 3.南 4.北. 4.东南 5. 东北 6.西北 7.西南")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "房屋朝向未选择!")
	@Pattern(groups = {addShopValidate.class, updateShopValidate.class},regexp = "^[东|南|西|北]{1,2}",message = "请选择正确的朝向")
	private String orientation;
	
	@ApiModelProperty(value = "电梯   0无 1有")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "是否有电梯不能为空")
	@Range(groups = {addShopValidate.class, updateShopValidate.class},min=0,max=1,message="你选择的电梯状态不符合")
	private Integer lift;
	
	@ApiModelProperty(value = "装修程度  0毛坯房 1普通装修  2精装修")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "装修程度不能为空")
	@Range(groups = {addShopValidate.class, updateShopValidate.class},min=0,max=2,message="请选择正确的装修程度")
	private Integer furnishingStyle;
	
	@ApiModelProperty(value = "年代")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "年代不能为空")
	@Pattern(groups = {addShopValidate.class,updateShopValidate.class}, regexp = RegexUtils.REGEX_YEAR , message = "房屋年代过于久远!")
	private String year;
	
	@ApiModelProperty(value = "用途 0普通住宅 1商业住宅")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "用途不能为空")
	@Range(groups = {addShopValidate.class, updateShopValidate.class},min=0,max=1,message="请选择正确的用途")
	private Integer purpose;
	
	@ApiModelProperty(value = "权属 0商品房 1学区房 2家住房")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "权属不能为空")
	@Range(groups = {addShopValidate.class, updateShopValidate.class},min=0,max=2,message="请选择正确的权属")
	private Integer ownership;
	
	@ApiModelProperty(value = "图片地址集合")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "请至少上传一张图片")
	private String[] imgPath;
	
	@ApiModelProperty(value = "经度")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "经度不能为空")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "纬度不能为空")
	private BigDecimal lat;
	
	@ApiModelProperty(value = "店铺地址")
	@NotBlank(groups = {addShopValidate.class, updateShopValidate.class}, message = "店铺地址不能为空")
	private String address;
	
	
	
	
	
	@ApiModelProperty(value = "商铺类型id集合")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "请至少选择一个类型")
	private Long[] shopTypeIds;
	
	@ApiModelProperty(value = "商铺类型名称集合")
	private List<String> shopTypeNames;
	
	@ApiModelProperty(value = "商铺行业id集合")
	@NotNull(groups = {addShopValidate.class, updateShopValidate.class}, message = "请至少选择一个行业")
	private Long[] shopBusinessIds;
	
	@ApiModelProperty(value = "商铺行业名称集合")
	private List<String> shopBusinessNames;
	
	@ApiModelProperty(value = "来源 0个人 1业主 2不限")
	private Integer source;
	
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
