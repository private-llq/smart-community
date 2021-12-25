package com.jsy.community.qo.sys;

import lombok.Data;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:01
 **/
@Data
public class SysMenuQO {
	private Long id;//ID
	private String icon;//菜单图标
	private String name;//菜单名
	private String path;//菜单url
	private Integer sort;//排序
	private Long pid;//父级id
	private Integer loginType;// 登录类型 1.大后台 2.物业 3.小区
	private Long updateId;
}
