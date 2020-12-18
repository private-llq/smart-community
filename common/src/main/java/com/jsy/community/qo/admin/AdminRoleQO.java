package com.jsy.community.qo.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:41
 **/
@Data
public class AdminRoleQO implements Serializable {
	private Long id;//ID
	private String name;//角色名
	private String remark;//备注
}
