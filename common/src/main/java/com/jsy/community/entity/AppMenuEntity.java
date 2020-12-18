package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @return
 * @Author lihao
 * @Description 菜单
 * @Date 2020/11/28 10:06
 * @Param
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_app_menu")
@ApiModel(value="AdminMenu对象", description="菜单")
public class AppMenuEntity extends BaseEntity {

    @ApiModelProperty(value = "社区id")
    @NotNull(groups = {addAdmin.class,updateAdmin.class},message = "社区id不能为空")
    private Long communityId;

    @ApiModelProperty(value = "父id 默认0")
    private Long parentId;

    @ApiModelProperty(value = "菜单状态 0 展示在首页  1 不展示在首页  默认1")
    private Integer status;

    @ApiModelProperty(value = "菜单名")
    @NotBlank(groups = {addAdmin.class,updateAdmin.class},message = "菜单名不能为空")
    private String menuName;

    @ApiModelProperty(value = "图标地址")
    private String icon;

    @ApiModelProperty(value = "路径地址")
    private String path;

    @ApiModelProperty(value = "描述信息")
    private String descr;
    
    @ApiModelProperty(value = "首页展示位置")
    private Integer sort;
    
    public interface addAdmin{}
    
    public interface updateAdmin{}

}