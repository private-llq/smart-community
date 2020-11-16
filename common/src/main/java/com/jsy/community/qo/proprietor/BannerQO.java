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
@ApiModel(value="Visitor查询、修改入参对象", description="轮播图")
public class BannerQO implements Serializable {
	
	@ApiModelProperty(value = "社区ID")
	@NotNull(groups = {queryBannerValidatedGroup.class}, message = "缺少社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "Banner位置1.顶部 2.底部")
	@NotNull(groups = {queryBannerValidatedGroup.class}, message = "缺少Banner位置")
	private Integer position;
	
	@ApiModelProperty(value = "要删除的数据id集合")
	private List<Long> ids;
	
	/**
	 * 新增访客验证组
	 */
	public interface queryBannerValidatedGroup{}
	
}
