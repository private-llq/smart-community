package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("t_index_menu")
@ApiModel(value="FrontMenu对象", description="首页菜单")
public class IndexMenuEntity extends BaseEntity {
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    
    @ApiModelProperty(value = "父id")
    private Long parentId;
    
    @ApiModelProperty(value = "菜单状态 0 首页 1不展示在首页")
    private Integer status;
    
    @ApiModelProperty(value = "菜单名")
    private String menuName;

    @ApiModelProperty(value = "白天图标地址")
    private String dayIcon;
    
    @ApiModelProperty(value = "夜晚图标地址")
    private String nightIcon;

    @ApiModelProperty(value = "路径地址")
    private String path;

    @ApiModelProperty(value = "描述信息")
    private String descr;

}
