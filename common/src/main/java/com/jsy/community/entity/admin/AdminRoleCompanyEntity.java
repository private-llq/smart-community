package com.jsy.community.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

/**
 * @author DKS
 * @description 角色与物业公司关联
 * @since 2021-12-16 15:13
 **/
@Data
@TableName("t_admin_role_company")
public class AdminRoleCompanyEntity extends BaseEntity {
	/**
	 * 角色id
	 */
	private Long roleId;
	/**
	 * 物业公司id
	 */
	private Long companyId;
}
