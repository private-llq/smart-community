package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单
 * </p>
 *
 * @author jsy
 * @since 2020-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_front_menu")
@ApiModel(value="FrontMenu对象", description="菜单")
public class FrontMenuEntity extends BaseEntity {

    @ApiModelProperty(value = "菜单名")
    private String menuName;

    @ApiModelProperty(value = "图标地址")
    private String icon;

    @ApiModelProperty(value = "路径地址")
    private String path;

    @ApiModelProperty(value = "描述信息")
    private String desc;

    @ApiModelProperty(value = "优先级")
    private Integer sort;

    @ApiModelProperty(value = "父id")
    private Long parentId;

}
