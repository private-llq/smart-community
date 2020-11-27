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
 * @since 2020-11-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_admin_menu")
@ApiModel(value="AdminMenu对象", description="菜单")
public class AdminMenuEntity extends BaseEntity {

    @ApiModelProperty(value = "社区id")
    private Long communityId;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "菜单状态 0 展示在首页  1 不展示在首页")
    private Integer status;

    @ApiModelProperty(value = "菜单名")
    private String menuName;

    @ApiModelProperty(value = "图标地址")
    private String icon;

    @ApiModelProperty(value = "路径地址")
    private String path;

    @ApiModelProperty(value = "描述信息")
    private String descr;

    private Integer sort;

}
