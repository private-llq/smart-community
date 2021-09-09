package com.jsy.community.vo.proprietor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.jsy.community.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

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
}
