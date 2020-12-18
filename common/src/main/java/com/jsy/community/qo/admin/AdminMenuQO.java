package com.jsy.community.qo.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:01
 **/
@Data
public class AdminMenuQO implements Serializable {
	private Long id;//ID
	private String icon;//菜单图标
	private String name;//菜单名
	private String url;//菜单url
	private Integer sort;//排序
	private Long pid;//父级id
}
