package com.jsy.community.qo.proprietor;

import com.jsy.community.entity.BannerEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chq459799974
 * @Date 2020/11/16 11:44
 **/
@Data
@ApiModel(description="轮播图")
public class BannerQO implements Serializable {
	@ApiModelProperty(value = "ID")
	private Long id;
	
	@ApiModelProperty(value = "社区ID")
	@NotNull(groups = {queryBannerValidatedGroup.class}, message = "缺少社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "Banner位置1.顶部 2.底部")
	@NotNull(groups = {queryBannerValidatedGroup.class}, message = "缺少Banner位置")
	private Integer position;
	
	@ApiModelProperty(value = "轮播图类型1.非广告 2.广告")
	private Integer type;
	
	@ApiModelProperty(value = "点击量排序 1.升序 -1.降序")
	private int clickOrder;
	
	@ApiModelProperty(value = "跳转路径")
	private String path;
	
	@ApiModelProperty(value = "描述")
	private String description;
	
	/**
	 * APP端查询轮播图验证组
	 */
	public interface queryBannerValidatedGroup{}
	
}
