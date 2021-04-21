package com.jsy.community.qo.proprietor;

import com.jsy.community.entity.BannerEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

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
	@NotNull(groups = updateBannerValidatedGroup.class, message = "缺少ID")
	private Long id;
	
	@ApiModelProperty(value = "社区ID")
	@NotNull(groups = {queryBannerValidatedGroup.class}, message = "缺少社区ID")
	private Long communityId;
	
	@ApiModelProperty(value = "Banner位置1.顶部 2.底部")
	private Integer position;
	
	@ApiModelProperty(value = "轮播图类型1.站内 2.外部链接")
	@Range(groups = updateBannerValidatedGroup.class, min = 1, max = 2, message = "站内/外部链接 类型选择错误")
	private Integer type;
	
	@ApiModelProperty(value = "点击量排序 1.升序 -1.降序")
	private int clickOrder;
	
	@ApiModelProperty(value = "标题")
	private String title;
	
	@ApiModelProperty(value = "内容(文字或外部链接)")
	private String content;
	
	@ApiModelProperty(value = "封面路径")
	private String url;
	
	@ApiModelProperty(value = "发布类型 0.草稿 1.已发布")
	@Range(groups = updateBannerValidatedGroup.class, min = 0, max = 1, message = "保存草稿/发布 操作选择错误")
	private Integer publishType;
	
	@ApiModelProperty(value = "状态 0.已撤销 1.发布中")
	@Range(groups = updateBannerValidatedGroup.class, min = 0, max = 1, message = "发布/撤销 状态选择错误")
	private Integer status;
	
	@ApiModelProperty(value = "操作人", hidden = true)
	private String operator;
	
	/**
	 * 物业端修改轮播图验证组
	 */
	public interface updateBannerValidatedGroup{}
	
	/**
	 * APP端查询轮播图验证组
	 */
	public interface queryBannerValidatedGroup{}
	
}
