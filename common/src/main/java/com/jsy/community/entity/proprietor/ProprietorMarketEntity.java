package com.jsy.community.entity.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("社区集市实体类")
@EqualsAndHashCode(callSuper = false)
@TableName("t_proprietor_market")
public class ProprietorMarketEntity extends BaseEntity {

    @ApiModelProperty(value = "发布人uid")
    private String uid;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;

    @ApiModelProperty(value = "商品名")
    private String goodsName;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;


    @ApiModelProperty(value = "商品说明")
    private String goodsExplain;

    @ApiModelProperty(value = "是否面议（0不面议 1面议  默认1）")
    private Integer negotiable;

    @ApiModelProperty(value = "标签id")
    private String labelId;

    @ApiModelProperty(value = "上下架（0下架  1上架）")
    private Integer state;

    @ApiModelProperty(value = "屏蔽（0未屏蔽  1已屏蔽）")
    private Integer shield;

    @ApiModelProperty(value = "商品类别id")
    private String categoryId;

    @ApiModelProperty(value = "点击率")
    private Integer click;


    @ApiModelProperty(value = "手机号")
    private String phone;


    @ApiModelProperty(value = "图片")
    private String images;

    @TableField(exist = false)
    @ApiModelProperty("标签名")
    private String labelName;

    @ApiModelProperty("类别名")
    @TableField(exist = false)
    private String categoryName;

    @ApiModelProperty("业主名")
    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    @ApiModelProperty(value = "社区名")
    private  String communityName;

    @ApiModelProperty(value = "前端删除")
    private Integer deleted;


    @ApiModelProperty(value = "后端删除")
    private Integer remove;



}
