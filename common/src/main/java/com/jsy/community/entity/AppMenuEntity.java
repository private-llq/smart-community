package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value="AppMenu对象", description="App菜单")
public class AppMenuEntity extends BaseEntity {

    @ApiModelProperty(value = "菜单名")
    @NotBlank(groups = {addAdmin.class,updateAdmin.class},message = "菜单名不能为空")
    private String menuName;

    @ApiModelProperty(value = "黑夜图标地址")
    @NotBlank(groups = {addAdmin.class,updateAdmin.class},message = "黑夜图标不能为空")
    private String nightIcon;
    
    @ApiModelProperty(value = "白天图标地址")
    @NotBlank(groups = {addAdmin.class,updateAdmin.class},message = "白天图标不能为空")
    private String daytimeIcon;
    
    @ApiModelProperty(value = "路径地址")
    @NotBlank(groups = {addAdmin.class,updateAdmin.class},message = "路径地址不能为空")
    private String path;

    @ApiModelProperty(value = "描述信息")
    private String descr;
    
    @ApiModelProperty(value = "社区id")
    @NotNull(groups = {addAdmin.class},message = "社区id不能为空")
    @TableField(exist = false)
    private Long communityId;
    
    @ApiModelProperty(value = "排序序号")
    @NotNull(groups = {addAdmin.class},message = "序号不能为空 ")
    @TableField(exist = false)
    private Long sort;
    
    @ApiModelProperty(value = "是否被选中[标志]")
    @TableField(exist = false)
    private Long checked;
    
    public interface addAdmin{}
    
    public interface updateAdmin{}

}
