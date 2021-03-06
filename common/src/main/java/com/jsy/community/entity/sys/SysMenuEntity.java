package com.jsy.community.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:01
 **/
@Data
@TableName("t_sys_menu")
public class SysMenuEntity extends BaseEntity {
	
	private String icon;//菜单图标
	
	@NotBlank(message = "菜单名不能为空")
	private String name;//菜单名
	private String path;//菜单url
	private Integer sort;//排序
	private Long pid;//父级id
	@JsonIgnore
	private Long belongTo;//顶级菜单id
	private Integer type;//类型 1.目录 2.菜单 3.按钮
	private Integer loginType;// 登录类型 1.大后台 2.物业 3.小区
	
	@TableField(exist = false)
	private List<SysMenuEntity> childrenList;//子菜单
	
	private Long createBy;//创建人
	private Long updateBy;//修改人
}
