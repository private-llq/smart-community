package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Banner对象", description="banner轮播图")
@TableName("t_banner")
public class BannerEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区ID")
    @NotNull(groups = {addBannerValidatedGroup.class}, message = "缺少社区ID")
    private Long communityId;

    @ApiModelProperty(value = "文件路径", hidden = true)
    @NotBlank(groups = {addBannerValidatedGroup.class}, message = "缺少图片路径")
    private String url;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "轮播排序12345")
    @NotNull(groups = {addBannerValidatedGroup.class}, message = "缺少轮播排序")
    private Integer sort;

    @ApiModelProperty(value = "Banner位置1.顶部 2.底部")
    @NotNull(groups = {addBannerValidatedGroup.class}, message = "缺少Banner位置")
    private Integer position;
    
    @ApiModelProperty(value = "轮播图类型1.非广告 2.广告")
    private Integer type;
    
    @ApiModelProperty(value = "点击量", hidden = true)
    private Integer click;
    
    /**
     * 新增访客验证组
     */
    public interface addBannerValidatedGroup{}
}
