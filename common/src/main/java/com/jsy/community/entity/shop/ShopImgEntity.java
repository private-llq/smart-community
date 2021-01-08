package com.jsy.community.entity.shop;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author jsy
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_shop_img")
@ApiModel(value="ShopImg对象", description="店铺图片")
public class ShopImgEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @TableId
    private Long id;

    @ApiModelProperty(value = "商铺id")
    private Long shopId;

    @ApiModelProperty(value = "店铺图片地址")
    private String imgUrl;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}
