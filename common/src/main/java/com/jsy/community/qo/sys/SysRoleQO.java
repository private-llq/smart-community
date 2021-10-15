package com.jsy.community.qo.sys;

import lombok.Data;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:41
 **/
@Data
public class SysRoleQO {
	private Long id;//ID
	private String name;//角色名
	private String remark;//备注
	private List<Long> menuIds;//菜单ID列表
}
