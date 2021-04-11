package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Long communityId;

    @ApiModelProperty(value = "封面路径")
    @NotBlank(groups = {addBannerValidatedGroup.class}, message = "缺少封面url")
    private String url;

    @ApiModelProperty(value = "轮播排序",hidden = true)
    private Integer sort;

    @ApiModelProperty(value = "Banner位置1.顶部 2.底部")
    @Range(min = 1, max = 2, message = "轮播图位置错误")
    private Integer position;
    
    @ApiModelProperty(value = "轮播图类型1.站内 2.外部链接")
    @NotNull(groups = addBannerValidatedGroup.class, message = "类型为空")
    @Range(min = 1, max = 2, message = "站内/外部链接 类型选择错误")
    private Integer type;
    
    @ApiModelProperty(value = "点击量(浏览数)", hidden = true)
    private Integer click;
    
    @ApiModelProperty(value = "标题")
    @NotBlank(groups = addBannerValidatedGroup.class, message = "标题为空")
    private String title;
    
    @ApiModelProperty(value = "内容(文字或外部链接)")
    @NotBlank(groups = addBannerValidatedGroup.class, message = "内容或外部链接为空")
    private String content;
    
    @ApiModelProperty(value = "发布类型 0.草稿 1.已发布")
    @NotNull(groups = addBannerValidatedGroup.class, message = "缺少发布类型 保存草稿/直接发布")
    @Range(groups = addBannerValidatedGroup.class, min = 0, max = 1, message = "状态不正确")
    private Integer publishType;
    
    @ApiModelProperty(value = "状态 0.已撤销 1.发布中")
    private Integer status;
    
    @ApiModelProperty(value = "创建人")
    private String createBy;
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    @ApiModelProperty(value = "发布人")
    private String publishBy;
    
    @ApiModelProperty(value = "发布时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime publishTime;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "创建时间-开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createDateStart;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "创建时间-结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createDateEnd;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "发布时间-开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate publishDateStart;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "发布时间-结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate publishDateEnd;
    
    /**
     * 新增轮播图验证组
     */
    public interface addBannerValidatedGroup{}
}
