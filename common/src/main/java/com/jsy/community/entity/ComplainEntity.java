package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-23 11:01
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_complain")
@ApiModel(value = "complain对象", description = "接收社区投诉建议")
public class ComplainEntity implements Serializable {
    @ApiModelProperty(value = "投诉建议id")
    private Long id;
    @TableField(exist = false)
    private String idStr;
    @ApiModelProperty(value = "编号")
    private String serialNumber;
    @ApiModelProperty(value = "社区id")
    private String communityId;

   /**
    * @Param:   新版本 没有这个字段  暂时注释掉
    *            加了一个新字段  电话号码
    * @Return:
    * @Author: Tian
    * @Date: 2021/8/20-10:53
    **/
    //@NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型，1投诉，2建议")
    private Integer type;
 
    /**
     * 来源（1.社区 2.商家）
     */
    private Integer source;
    
    @ApiModelProperty(value = "类型名称，1投诉，2建议")
    @TableField(exist = false)
    private String typeName;

    @NotNull(message = "类容不能为空")
    @ApiModelProperty(value = "内容")
    private String content;

    @NotNull(message = "电话不能为空")
    @ApiModelProperty(value = "电话")
    private String phone;


    @ApiModelProperty(hidden = true)
    @TableLogic
    private Long deleted;
    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "图片地址")
    private String images;
    @TableField(exist = false)
    private List<String> imageList;
    @ApiModelProperty(value = "投诉人")
    private String uid;
    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;
    @ApiModelProperty(value = "1，已回复，0未回复")
    private Integer status;
    @ApiModelProperty(value = "反馈内容")
    private String feedbackBy;
    @ApiModelProperty(value = "反馈内容")
    private String feedbackContent;
    @ApiModelProperty(value = "反馈时间")
    private LocalDateTime feedbackTime;
}
