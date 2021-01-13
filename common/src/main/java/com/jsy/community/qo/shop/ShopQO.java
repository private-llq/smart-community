package com.jsy.community.qo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "城市id不能为空")
	private Long cityId;
	
	@ApiModelProperty(value = "区域id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "区域id不能为空")
	private Long areaId;
	
	@ApiModelProperty(value = "社区id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "社区id不能为空")
	private Long communityId;
	
	@ApiModelProperty(value = "楼层")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "楼层不能为空")
	private String floor;
	
	@ApiModelProperty(value = "店铺面积  单位：平米")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "店铺面积不能为空")
	private Double shopAcreage;
	
	@ApiModelProperty(value = "面宽")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "面宽不能为空")
	private Double shopWidth;
	
	@ApiModelProperty(value = "进深")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "进深不能为空")
	private Double shopDepth;
	
	@ApiModelProperty(value = "层高")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "层高不能为空")
	private Double shopHeight;
	
	@ApiModelProperty(value = "标题")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "标题不能为空")
	private String title;
	
	@ApiModelProperty(value = "概述")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "概述不能为空")
	private String summarize;
	
	@ApiModelProperty(value = "月租金")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "月租金不能为空")
	private BigDecimal monthMoney;
	
	@ApiModelProperty(value = "转让费")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "转让费不能为空")
	private BigDecimal transferMoney;
	
	@ApiModelProperty(value = "免租月数")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "免租月数不能为空")
	private Integer freeLease;
	
	@ApiModelProperty(value = "起租月数")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "起租月数不能为空")
	private Integer startLease;
	
	@ApiModelProperty(value = "押付方式")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "押付方式不能为空")
	@Pattern(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class},regexp = "^押([0-9]{1,2})付([0-9]{1,2})$",message = "请选择正确的押付方式")
	private String defrayType;
	
	@ApiModelProperty(value = "店铺状态 0空置中 1经营中")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "店铺状态不能为空")
	private Integer status;
	
	@ApiModelProperty(value = "经度")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "经度不能为空")
	private BigDecimal lon;
	
	@ApiModelProperty(value = "纬度")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "纬度不能为空")
	private BigDecimal lat;
	
	@ApiModelProperty(value = "来源 0个人 1物业")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "来源不能为空")
	private Integer source;
	
	@ApiModelProperty(value = "称呼")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "称呼不能为空")
	private String nickname;
	
	@ApiModelProperty(value = "手机")
	@NotBlank(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "手机号不能为空")
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
	
	@ApiModelProperty(value = "所属商业id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "请选择所属行业")
	private Long shopBusinessId;
	
	@ApiModelProperty(value = "所属类型id")
	@NotNull(groups = {ShopQO.addShopValidate.class, ShopQO.updateShopValidate.class}, message = "请选择所属类型")
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
