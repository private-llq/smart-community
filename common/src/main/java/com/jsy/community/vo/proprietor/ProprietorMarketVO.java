package com.jsy.community.vo.proprietor;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("我的发布商品返回参数")
@EqualsAndHashCode(callSuper = false)
public class ProprietorMarketVO extends BaseVO {
    @ApiModelProperty(value = "商品名")
    private String goodsName;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "图片")
    private String images;

    @ApiModelProperty(value = "商品说明")
    private String goodsExplain;

    @ApiModelProperty(value = "是否面议（0不面议 1面议  默认1）")
    private Integer negotiable;

    @ApiModelProperty(value = "上下架（0下架  1上架）")
    private Integer state;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @TableField(exist = false)
    @ApiModelProperty("标签名")
    private String labelName;

    @ApiModelProperty("类别名")
    @TableField(exist = false)
    private String categoryName;

    @ApiModelProperty("业主名")
    @TableField(exist = false)
    private String realName;

    @ApiModelProperty(value = "标签id")
    private String labelId;

    @ApiModelProperty(value = "商品类别id")
    private String categoryId;
    @ApiModelProperty(value = "点击率")
    private Integer click;

    @ApiModelProperty(value = "前端删除")
    private Integer deleted;


    @ApiModelProperty(value = "后端删除")
    private Integer remove;

    @ApiModelProperty(value = "屏蔽（0未屏蔽  1已屏蔽）")
    private Integer shield;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @TableField(exist = false)
    @ApiModelProperty(value = "社区名")
    private  String communityName;


    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;
}
