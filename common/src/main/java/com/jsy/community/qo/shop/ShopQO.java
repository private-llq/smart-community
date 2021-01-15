package com.jsy.community.qo.shop;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lihao
 * @ClassName ShopQO
 * @Date 2021/1/11  15:25
 * @Description 店铺发布
 * @Version 1.0
 **/
@Data
@ApiModel("店铺发布")
public class ShopQO implements Serializable {
	@ApiModelProperty(value = "业主id")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "业主id不能为空")
	private String uid;
	
	@ApiModelProperty(value = "城市id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "所在城市不能为空")
	private Long cityId;
	
	@ApiModelProperty(value = "区域id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "所在区域不能为空")
	private Long areaId;
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "商铺地址不能为空")
	private Long communityId;
	
	/**
	 * 匹配文本： 2层/共18层  或者 共18层 或者 1层至3层/共18层 这样的格式字符串
	 */
	@Pattern(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, regexp = "^([(-1)-9]{1,2})层/共([0-9]{1,2})层$|^共[1-9]{1,2}层$|^([(-1)-9]{1,2})层至[1-9]{1,2}层/共([0-9]{1,2})层$", message = "请输入正确的楼层文本!")
	@ApiModelProperty(value = "楼层")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "商铺楼层不能为空")
	private String floor;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "建筑面积不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "1.00",message = "面积最少为1.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "99999.99",message = "面积最大为99999.99")
	private Double shopAcreage;
	
	@ApiModelProperty(value = "面宽")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "面宽不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "1.00",message = "面宽最少为1.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "999.99",message = "面宽最大为999.99")
	private Double shopWidth;
	
	@ApiModelProperty(value = "进深")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "进深不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "1.00",message = "进深最少为1.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "999.99",message = "进深最大为999.99")
	private Double shopDepth;
	
	@ApiModelProperty(value = "层高")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "层高不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "1.00",message = "进深最少为1.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "999.99",message = "进深最大为99.99")
	private Double shopHeight;
	
	@ApiModelProperty(value = "标题")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "标题不能为空")
	@Length(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},min = 1,max = 20,message = "标题最大长度为20个字符")
	private String title;
	
	@ApiModelProperty(value = "概述")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "概述不能为空")
	@Length(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},min = 1,max = 500,message = "概述最大长度为500个字符")
	private String summarize;
	
	@ApiModelProperty(value = "月租金")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "月租金不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "1.00",message = "月租金最少为1.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "99999.99",message = "月租金最大为99999.99")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "转让费")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "转让费不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "1.00",message = "转让费最少为1.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "9999999.99",message = "转让费最大为9999999.99")
	private BigDecimal transferMoney;
	
	@ApiModelProperty(value = "免租月数")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "免租月数不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "0.00",message = "免租月数最少为0.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "99.00",message = "免租月数最大为99")
	private Integer freeLease;
	
	@ApiModelProperty(value = "起租月数")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "起租月数不能为空")
	@DecimalMin(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "0.00",message = "起租月数最少为0.00")
	@DecimalMax(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},value = "99.00",message = "起租月数最大为99")
	private Integer startLease;
	
	@ApiModelProperty(value = "押付方式")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "押付方式不能为空")
	@Pattern(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},regexp = "^押([0-9]{1,2})付([0-9]{1,2})$",message = "请选择正确的押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "店铺状态 0空置中 1营业中")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "商铺状态不能为空")
	@Range(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},min = 0,max = 1,message = "店铺状态只能是空置中或营业中")
	private Integer status;
	
	@ApiModelProperty(value = "经度")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "经度不能为空")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "纬度不能为空")
	private BigDecimal lat;
	
	@ApiModelProperty(value = "来源 1业主 2物业")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "来源不能为空")
	@Range(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},min = 0,max = 1,message = "店铺来源只能是业主或物业")
	private Integer source;
	
	@ApiModelProperty(value = "称呼")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "称呼不能为空")
	@Length(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},min = 1,max = 10,message = "称呼最大长度为10个字符")
	private String nickname;
	
	@ApiModelProperty(value = "手机")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "手机号不能为空")
	@Pattern(groups = {ShopQO.addShopValidate.class},regexp = RegexUtils.REGEX_MOBILE,message = "您输入的联系电话不符合要求")
	private String mobile;
	
	@ApiModelProperty(value = "图片地址集合")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "请至少上传一张图片")
	private String[] imgPath;
	

	
	
	@ApiModelProperty(value = "客流人群Code")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "请至少选择一个客流人群")
	private List<Long> shopPeoples;
	
	@ApiModelProperty(value = "配套设施Code")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "请至少选择一个配套设施")
	private List<Long> shopFacilityList;
	
	@ApiModelProperty(value = "所属行业id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "商铺行业不能为空")
	private Long shopBusinessId;
	
	@ApiModelProperty(value = "所属类型id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "商铺类型不能为空")
	private Long shopTypeId;
	
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
